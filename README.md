# Graph-Based Data Modeling and Query System

## Overview

This project is a submission for a Forward Deployed Engineer task.

The goal is to build a graph-based business data exploration system with a natural language query interface. The system should ingest structured business data, model it as a graph, visualize relationships between entities, and allow users to ask questions in natural language. The application should use an LLM to translate valid business questions into SQL, execute the query on the database, and return grounded answers based on actual data.

This project should be built with **Spring Boot** for the backend because that is the preferred stack for implementation.

## What We Are Building

We are building a system with three core capabilities:

1. **Business Data Modeling as a Graph**
   - Represent business entities such as customers, orders, deliveries, invoices, payments, and products as graph nodes.
   - Represent relationships between them as graph edges.
   - Example relationships:
     - Customer -> Order
     - Order -> Delivery
     - Order -> Invoice
     - Invoice -> Payment
     - Order -> Product

2. **Graph Visualization UI**
   - Show nodes and edges in an interactive graph.
   - Allow users to expand nodes and inspect metadata.
   - Support exploring relationship flows across the business process.

3. **Natural Language Query Interface**
   - User asks a business question in plain English.
   - Backend sends schema-aware context to an LLM.
   - LLM generates a safe read-only SQL query.
   - Backend validates and executes the SQL.
   - Query results are sent back to the LLM for grounded answer formatting.
   - Final answer is returned to the user.

## Core User Experience

The user should be able to:

- explore the graph visually
- inspect entity details and relationships
- ask questions like:
  - "Trace the full flow for a billing document"
  - "Which products are associated with the highest number of billing documents?"
  - "Find order flows where payment is missing after invoice generation"
- receive answers that are backed by data from the database

## Target Architecture

### Backend

The backend should be built using **Spring Boot**.

Suggested responsibilities:

- ingest the dataset into a relational database
- expose APIs for graph data
- expose APIs for chat-based natural language querying
- call the LLM for SQL generation and answer formatting
- validate generated SQL before execution
- reject unrelated or unsafe prompts

### Database

Use a relational database as the source of truth.

Recommended options:

- **PostgreSQL** for a stronger production-style implementation
- **H2/SQLite** only if a faster local MVP is needed

Use:

- **Spring Data JPA** for entity modeling and standard access
- **JdbcTemplate** for executing LLM-generated SQL safely

### Frontend

Frontend can be a lightweight web app, ideally React-based.

Suggested UI layout:

- graph panel for node/edge visualization
- chat panel for natural language questions
- optional node highlighting based on query results

## Suggested Domain Model

Potential core entities:

- Customer
- Order
- OrderItem
- Product
- Delivery
- Invoice
- Payment

Potential graph relationships:

- Customer -> Order
- Order -> OrderItem
- OrderItem -> Product
- Order -> Delivery
- Order -> Invoice
- Invoice -> Payment

## NL to SQL Workflow

The chat system should follow this pipeline:

1. User submits a question.
2. Backend checks whether the question is relevant to the business dataset.
3. Backend builds a prompt containing:
   - database schema
   - table relationships
   - guardrail instructions
   - the user question
4. LLM generates SQL.
5. Backend validates the SQL.
6. Backend executes SQL against the database.
7. Backend sends results to the LLM for response formatting.
8. User receives a grounded natural language answer.

## SQL Safety Requirements

Generated SQL must be restricted.

Allowed:

- `SELECT` queries only
- known tables only
- known columns only if feasible to enforce

Not allowed:

- `INSERT`
- `UPDATE`
- `DELETE`
- `DROP`
- `ALTER`
- multi-statement SQL
- unrelated data access

The backend should reject unsafe SQL before execution.

## Guardrails

The chatbot must reject prompts that are outside the scope of the dataset.

Examples of prompts to reject:

- general knowledge questions
- creative writing requests
- coding help requests
- personal advice
- any question not grounded in the available business data

Expected rejection style:

- brief
- polite
- clear that the system only answers questions about the uploaded dataset/business graph

## API Ideas

Suggested backend endpoints:

- `GET /api/graph`
  - returns graph nodes and edges

- `GET /api/graph/node/{id}`
  - returns detailed metadata for a node

- `POST /api/chat/query`
  - accepts a user question
  - returns generated SQL, result rows if needed, and final formatted answer

- `GET /api/schema`
  - optional endpoint for debugging schema context

## Suggested Spring Boot Package Structure

```text
src/main/java/com/example/graphquerysystem
  controller/
  service/
  repository/
  entity/
  dto/
  config/
  util/
```

Suggested services:

- `GraphService`
- `GraphQueryService`
- `LlmService`
- `SchemaContextService`
- `SqlGenerationService`
- `SqlValidationService`
- `QueryExecutionService`
- `AnswerFormattingService`

Suggested controllers:

- `GraphController`
- `ChatController`

## Implementation Priorities

Build in this order:

1. Create Spring Boot project structure
2. Define relational schema and load dataset
3. Build entity and repository layer
4. Expose graph data APIs
5. Build graph visualization frontend
6. Add natural language query API
7. Integrate LLM-based SQL generation
8. Add SQL validation and guardrails
9. Format grounded answers
10. Polish demo and README

## Bonus Features

If time permits, add:

- streaming LLM responses
- chat memory for follow-up questions
- graph clustering/grouping
- semantic search over node metadata
- automatic graph node highlighting from query results

## Submission Expectations

The final submission should include:

- working demo
- public GitHub repository
- clear README
- architecture explanation
- database choice rationale
- prompting strategy
- guardrail design
- AI coding logs or transcripts showing how AI tools were used

## Notes for AI Coding Assistants

Use this README as the primary project brief.

Important expectations:

- keep the backend in Spring Boot
- prefer a practical MVP over an over-engineered design
- model the business data relationally, then expose graph-shaped APIs
- ensure the LLM never executes raw SQL without validation
- all answers must be grounded in the actual dataset
- prioritize speed of delivery, clarity of architecture, and demo reliability

When generating code, prefer:

- clear package boundaries
- DTO-based APIs
- service-oriented backend structure
- safe SQL execution patterns
- simple, demo-friendly frontend integration

## Current Direction

Current preferred stack:

- Backend: Spring Boot
- Database: PostgreSQL preferred
- Data access: Spring Data JPA + JdbcTemplate
- Frontend: React
- Graph UI: React Flow or Cytoscape
- LLM integration: to be configured based on selected provider

## Definition of Done

This project is complete when:

- business data is loaded into the database
- graph nodes and edges can be fetched and visualized
- user can ask valid business questions in natural language
- system converts the question to safe SQL
- SQL executes successfully
- answer is returned based on real data
- off-topic prompts are rejected
- demo is stable enough to present
