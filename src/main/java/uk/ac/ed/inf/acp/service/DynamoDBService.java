package uk.ac.ed.inf.acp.service;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.thirdparty.jackson.core.JsonProcessingException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.core.type.TypeReference;
import uk.ac.ed.inf.acp.model.Drone;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
@Service
public class DynamoDBService {
    public final String SID = "s2347484";

    public final DynamoDbClient dynamoDbClient;

    public DynamoDBService(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    private Map<String, Object> convertItemToMap(Map<String, AttributeValue> item) {
        Map<String, Object> result = new LinkedHashMap<>();
        item.forEach((key, value) -> result.put(key, convertAttributeValue(value)));
        return result;
    }

    private Object convertAttributeValue(AttributeValue av) {
        if (av.s() != null)    return av.s();
        if (av.n() != null)    return new BigDecimal(av.n());
        if (av.bool() != null) return av.bool();
        if (av.hasM())         return convertItemToMap(av.m());
        if (av.hasL())         return av.l().stream()
                .map(this::convertAttributeValue)
                .collect(Collectors.toList());
        return null;
    }

    public List<Map<String,Object>> getAllItems(String table) {
        List<Map<String, Object>> allItems = new ArrayList<>();
        Map<String, AttributeValue> lastEvaluatedKey = null;

        do {
            ScanRequest.Builder scanRequest = ScanRequest.builder().tableName(table);
            if (lastEvaluatedKey != null && !lastEvaluatedKey.isEmpty()) {
                scanRequest.exclusiveStartKey(lastEvaluatedKey);
            }

            ScanResponse scanResponse = dynamoDbClient.scan(scanRequest.build());

            scanResponse.items().stream().map(this::convertItemToMap).forEach(allItems::add);

            lastEvaluatedKey = scanResponse.lastEvaluatedKey();
        } while(lastEvaluatedKey != null && !lastEvaluatedKey.isEmpty());

        return allItems;
    }

    public Map<String, Object> getItem(String table, String key) {
        // Dynamically resolve the partition key name via DescribeTable
        DescribeTableRequest describeRequest = DescribeTableRequest.builder().tableName(table).build();
        String partitionKeyName = dynamoDbClient.describeTable(describeRequest)
                .table().keySchema().stream()
                .filter(k -> k.keyType() == KeyType.HASH)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No partition key found for table: " + table))
                .attributeName();

        GetItemRequest request = GetItemRequest.builder()
                .tableName(table)
                .key(Map.of(partitionKeyName, AttributeValue.builder().s(key).build()))
                .build();
        GetItemResponse itemResponse = dynamoDbClient.getItem(request);

        if (!itemResponse.hasItem()){
            return null;
        }
        return convertItemToMap(itemResponse.item());
    }

    private AttributeValue toAttributeValue(Object value) {
        if (value == null) return AttributeValue.builder().nul(true).build();
        if (value instanceof Boolean) return AttributeValue.builder().bool((Boolean) value).build();
        if (value instanceof String) return AttributeValue.builder().s((String) value).build();
        return AttributeValue.builder().n(value.toString()).build();
    }

    private AttributeValue objectToAttributeValue(Object value) {
        if (value == null) return AttributeValue.builder().nul(true).build();
        if (value instanceof Boolean) return AttributeValue.builder().bool((Boolean) value).build();
        if (value instanceof String) return AttributeValue.builder().s((String) value).build();
        if (value instanceof Number) return AttributeValue.builder().n(value.toString()).build();
        if (value instanceof Map) {
            Map<String, AttributeValue> m = new HashMap<>();
            ((Map<?, ?>) value).forEach((k, v) -> m.put(k.toString(), objectToAttributeValue(v)));
            return AttributeValue.builder().m(m).build();
        }
        if (value instanceof List) {
            List<AttributeValue> l = ((List<?>) value).stream()
                    .map(this::objectToAttributeValue)
                    .collect(Collectors.toList());
            return AttributeValue.builder().l(l).build();
        }
        return AttributeValue.builder().s(value.toString()).build();
    }

    public void saveMapToDynamo(String uuid, Map<String, Object> data) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("name", AttributeValue.builder().s(uuid).build());
        item.put("content", objectToAttributeValue(data));
        PutItemRequest request = PutItemRequest.builder()
                .tableName(SID)
                .item(item)
                .build();
        dynamoDbClient.putItem(request);
    }

    public void saveDroneToDynamo(Drone drone) throws JsonProcessingException {
        // Store the entire drone as a single DynamoDB Map attribute, keyed by drone name
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> droneMap = mapper.readValue(drone.toJson(), new TypeReference<Map<String, Object>>() {});

        Map<String, AttributeValue> item = new HashMap<>();
        item.put("name", AttributeValue.builder().s(drone.getName()).build());
        item.put("content", objectToAttributeValue(droneMap));

        PutItemRequest request = PutItemRequest.builder()
                .tableName(SID)
                .item(item)
                .build();
        dynamoDbClient.putItem(request);
    }
}
