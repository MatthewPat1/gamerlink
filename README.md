# 🎮 GamerLink

GamerLink is a modular backend platform inspired by LinkedIn, built specifically for gamers.
It focuses on identity, profiles, networking, and social interaction, with a strong emphasis on clean architecture, domain boundaries, and scalability-first design.

This project is primarily a learning-focused system and a portfolio piece, designed to demonstrate how a real-world platform can evolve from a modular monolith into independently scalable microservices.

## 🚀 Goals & Motivation

- Design a production-style backend with clear domain ownership
- Practice modular system architecture with a path to microservices
- Explore authentication, identity, and session management in depth
- Apply real-world backend patterns: event-driven communication, snapshotting, cursor pagination
- Build something complex enough to be meaningful — but maintainable

## 🧱 Architecture Overview

GamerLink is built as a modular monolith.

Each module:

- Owns its own domain and data
- Exposes clear API boundaries
- Avoids direct cross-module joins
- Can be extracted into a standalone microservice with minimal refactoring
- This approach allows fast local development while preserving a clean migration path to distributed services as the system grows.

Key architectural principles:

- Domain-driven module boundaries
- Application-generated UUIDv7 identifiers
- Flyway-managed database schema
- Explicit ownership of data and responsibilities
- Scalability and correctness prioritized over premature optimization

## 🧩 Core Modules
### Identity (Authentication & Core User Identity)

Responsible for authentication and minimal user identity.
- User registration and login
- JWT access and refresh token management
- Session tracking and token revocation
- Provides “Who am I?” identity information to other modules via userId
- Status: Actively implementing

### Profile (Public Gamer Profiles)

- Handles all public-facing user profile data.
- Handles, display names, bios, regions, and social links
- Gamer-specific data (games played, roles, ranks, platforms)
- Public profile viewing and editing your own profile
- Status: Actively Implementing

### Connections (Networking)

- Professional-style networking between users.
- Connection requests and acceptance
- Incoming/outgoing request views
- Mutual connections
- Personal network queries
- Status: Planned

### Feed (Posts & Engagement)

- Social content and engagement system.
- Posts, reactions, and comments
- Personalized feed based on connections
- Cursor-based pagination for scalability
- To avoid cross-module joins, the feed stores author snapshots (handle, display name, avatar) and listens for profile update events to refresh them.
- Status: Planned

### Media

- Asset metadata and media handling.
- External links (Twitch, YouTube)
- Upload lifecycle management
- Local storage for development
- S3-compatible storage for production
- Status: Planned

## 🔑 Key Design Decisions

- Modular-first design: clean separation of domains from day one
- No cross-module joins: modules communicate using IDs and events
- Snapshotting over live joins: improves performance and isolation
- Flyway as the source of truth: schema changes are versioned and explicit
- UUIDv7 identifiers: sortable, scalable IDs generated application-side
- Security-first identity design: minimal identity data shared across modules

## 🛠 Tech Stack

- Java / Spring Boot
- Spring Data JPA / Hibernate
- PostgreSQL
- Flyway (database migrations)
- Docker & Docker Compose
- JWT-based authentication
- Lombok (used selectively and safely)

## 📌 Current Status

- Identity module is in active development
- Other modules are designed and scaffolded
- Current focus is on correctness, security, and clean domain modeling before expanding feature breadth
- This project is evolving incrementally, with architecture and maintainability prioritized over rapid feature accumulation.

## 📚 Documentation

Detailed API designs and module-specific documentation are maintained separately and will be added to the repository as the system evolves.

## 🧠 Why This Project Exists

GamerLink is intentionally more complex than a typical tutorial project.
It is designed to reflect the kinds of architectural decisions, tradeoffs, and constraints found in real production systems.

The goal is not just to “make it work,” but to build it the right way.
