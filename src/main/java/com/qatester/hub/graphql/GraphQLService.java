package com.qatester.hub.graphql;

import graphql.ExecutionInput;
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
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class GraphQLService {

    private final GraphQL graphQL;
    private final Map<Integer, Map<String, Object>> users = new HashMap<>();
    private final List<Map<String, Object>> posts = new ArrayList<>();
    private final AtomicInteger postIdCounter = new AtomicInteger(11);

    public GraphQLService() {
        users.put(1, Map.of("id", 1, "name", "Alice"));
        users.put(2, Map.of("id", 2, "name", "Bob"));
        posts.add(Map.of("id", 10, "title", "Hello", "body", "First post", "author_id", 1));
        posts.add(Map.of("id", 11, "title", "Second", "body", "Post two", "author_id", 2));

        String sdl = """
            type Query { user(id: ID!): User, posts: [Post!]! }
            type Mutation { createPost(input: PostInput!): Post! }
            type User { id: ID!, name: String! }
            type Post { id: ID!, title: String!, body: String!, author: User! }
            input PostInput { title: String!, body: String! }
            """;

        TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(sdl);
        RuntimeWiring wiring = RuntimeWiring.newRuntimeWiring()
                .type("Query", builder -> builder
                        .dataFetcher("user", (DataFetcher<Map<String, Object>>) env -> 
                                users.get(Integer.parseInt(env.getArgument("id"))))
                        .dataFetcher("posts", (DataFetcher<List<Map<String, Object>>>) env -> {
                            List<Map<String, Object>> result = new ArrayList<>();
                            for (Map<String, Object> post : posts) {
                                Map<String, Object> p = new HashMap<>(post);
                                p.put("author", users.get(post.get("author_id")));
                                result.add(p);
                            }
                            return result;
                        }))
                .type("Mutation", builder -> builder
                        .dataFetcher("createPost", (DataFetcher<Map<String, Object>>) env -> {
                            Map<String, Object> input = env.getArgument("input");
                            int newId = postIdCounter.getAndIncrement();
                            Map<String, Object> newPost = new HashMap<>();
                            newPost.put("id", newId);
                            newPost.put("title", input.get("title"));
                            newPost.put("body", input.get("body"));
                            newPost.put("author_id", 1);
                            posts.add(newPost);
                            Map<String, Object> result = new HashMap<>(newPost);
                            result.put("author", users.get(1));
                            return result;
                        }))
                .build();
        
        GraphQLSchema schema = new SchemaGenerator().makeExecutableSchema(typeRegistry, wiring);
        this.graphQL = GraphQL.newGraphQL(schema).build();
    }

    public Map<String, Object> execute(String query, Map<String, Object> variables, String operationName) {
        ExecutionInput.Builder inputBuilder = ExecutionInput.newExecutionInput().query(query);
        if (variables != null) inputBuilder.variables(variables);
        if (operationName != null) inputBuilder.operationName(operationName);
        ExecutionResult result = graphQL.execute(inputBuilder.build());
        return result.toSpecification();
    }
}
