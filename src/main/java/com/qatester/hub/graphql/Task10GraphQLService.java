package com.qatester.hub.graphql;

import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.DataFetcher;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class Task10GraphQLService {

    private final GraphQL graphQL;

    public Task10GraphQLService() {
        String sdl = """
            enum DescType { MANUAL, AUTOMATIC, CORRECTION }
            enum Status { ACTIVE, INACTIVE, PENDING_REVIEW }

            input CompetitorInput {
                id: ID!
                name: String
            }

            type Event {
                id: ID!
                source: String
                status: Status
                scheduled: String
                competitors: [String]
                marketIds: [Int]
                descType: DescType!
            }

            type Mutation {
                editEvent(
                    id: ID!,
                    source: String,
                    status: Status,
                    scheduled: String,
                    competitors: [CompetitorInput],
                    marketIds: [Int],
                    priority: Int,
                    isLive: Boolean,
                    notes: String,
                    descType: DescType!
                ): Event
            }

            type Query {
                _empty: String
            }
            """;

        TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(sdl);
        RuntimeWiring wiring = buildWiring();
        GraphQLSchema schema = new SchemaGenerator().makeExecutableSchema(typeRegistry, wiring);
        this.graphQL = GraphQL.newGraphQL(schema).build();
    }

    private RuntimeWiring buildWiring() {
        return RuntimeWiring.newRuntimeWiring()
                .type("Mutation", builder -> builder
                        .dataFetcher("editEvent", (DataFetcher<Map<String, Object>>) env -> {
                            Map<String, Object> result = new HashMap<>();
                            result.put("id", env.getArgument("id"));
                            result.put("source", env.getArgument("source"));
                            result.put("status", env.getArgument("status"));
                            result.put("scheduled", env.getArgument("scheduled"));
                            result.put("descType", env.getArgument("descType"));
                            result.put("marketIds", env.getArgument("marketIds"));

                            // Process competitors
                            List<Map<String, Object>> competitors = env.getArgument("competitors");
                            if (competitors != null) {
                                List<String> names = new ArrayList<>();
                                for (Map<String, Object> c : competitors) {
                                    if (c.get("name") != null) {
                                        names.add((String) c.get("name"));
                                    }
                                }
                                result.put("competitors", names);
                            }

                            return result;
                        })
                )
                .type("Query", builder -> builder
                        .dataFetcher("_empty", env -> ""))
                .build();
    }

    public Map<String, Object> execute(String query, Map<String, Object> variables, String operationName) {
        ExecutionResult result = graphQL.execute(query, operationName, null, variables);
        return result.toSpecification();
    }
}
