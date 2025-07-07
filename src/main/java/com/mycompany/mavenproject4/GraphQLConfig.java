// package com.mycompany.mavenproject4;

// import graphql.GraphQL;
// import graphql.*;
// import graphql.schema.idl.*;
// import java.io.*;
// import java.util.Objects;
// import graphql.schema.GraphQLSchema;
// import javax.swing.*;
// import javax.swing.table.DefaultTableModel;
// import java.awt.*;
// import java.awt.event.*;

// public class GraphQLConfig {
//     public static GraphQL init() throws IOException {
//         InputStream schemaStream = GraphQLConfig.class.getClassLoader().getResourceAsStream("schema.graphqls");

//         if (schemaStream == null) {
//             throw new RuntimeException("schema.graphqls not found in classpath.");
//         }

//         String schema = new String(Objects.requireNonNull(schemaStream).readAllBytes());

//         TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(schema);
//         RuntimeWiring wiring = RuntimeWiring.newRuntimeWiring()
//             .type("Query", builder -> builder
//                 .dataFetcher("allProducts", env -> VisitLogRepository.findAll())
//                 .dataFetcher("productById", env -> {
//                     int id = int.parseLong(env.getArgument("id")); 
//                     return VisitLogRepository.findById(id);
//                 })
//             )
//             .type("Mutation", builder -> builder
//                 .dataFetcher("addProduct", env -> VisitLogRepository.add(
//                     env.getArgument("name"),
//                     ((Number) env.getArgument("price")).doubleValue(),
//                     env.getArgument("category")
//                 ))
//                 .dataFetcher("updateProduct", env -> VisitLogRepository.update(
//                     int.parseLong(env.getArgument("id")),
//                     env.getArgument("name"),
//                     ((Number) env.getArgument("price")).doubleValue(),
//                     env.getArgument("category")
//                 ))
//                 .dataFetcher("deleteProduct", env -> {
//                     int id = int.parseLong(env.getArgument("id")); 
//                     return VisitLogRepository.delete(id);
//                 })
//             )
//             .build();

//         GraphQLSchema schemaFinal = new SchemaGenerator().makeExecutableSchema(typeRegistry, wiring);
//         return GraphQL.newGraphQL(schemaFinal).build();
//     }
// }
