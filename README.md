# Qa3at - Wedding Halls Booking Platform

**قاعات كوتلاين** - A bilingual (Arabic/English) wedding halls and event venue booking platform.

## Overview

Qa3at is a production-grade mobile application and API for booking wedding halls, hotels, and related services in Saudi Arabia. The platform offers a hotel-like booking experience with package customization.

## Features

- **Venue Search & Booking**: Search venues by city, date, capacity, and time slot
- **Package Builder**: Customize your event with decoration, catering, photography, and music packages
- **AI Assistant**: Get personalized recommendations based on your preferences
- **Bilingual Support**: Full Arabic and English localization with RTL support
- **Secure Payments**: Multiple payment methods (Card, Mada, Apple Pay, Bank Transfer)

## Tech Stack

### Android App
- **Language**: Kotlin
- **UI**: Jetpack Compose (Material 3)
- **Architecture**: MVVM + Clean Architecture
- **DI**: Hilt
- **Networking**: Retrofit + OkHttp
- **Local Storage**: Room + DataStore
- **Image Loading**: Coil
- **Navigation**: Navigation Compose

### Backend API
- **Framework**: NestJS
- **ORM**: Prisma
- **Database**: PostgreSQL
- **Auth**: JWT + Passport
- **Docs**: Swagger/OpenAPI

## Project Structure

```
qa3at/
├── app/                          # Android app
│   ├── src/main/
│   │   ├── java/com/example/qa3at/
│   │   │   ├── data/            # Repositories, data sources
│   │   │   ├── domain/          # Models, use cases
│   │   │   ├── di/              # Hilt modules
│   │   │   └── ui/              # Compose screens, components
│   │   └── res/                 # Resources (strings, drawables)
│   └── build.gradle.kts
├── backend/                      # NestJS API
│   ├── prisma/                  # Database schema & migrations
│   │   ├── schema.prisma
│   │   └── seed.ts
│   └── src/
│       ├── auth/                # Authentication module
│       ├── venues/              # Venues module
│       ├── bookings/            # Bookings module
│       ├── packages/            # Packages module
│       └── assistant/           # AI assistant module
├── docs/                        # Documentation
└── README.md
```

## Getting Started

### Prerequisites

- **Android**: Android Studio Hedgehog+, JDK 17
- **Backend**: Node.js 18+, PostgreSQL 14+

### Android Setup

1. Open the project in Android Studio
2. Sync Gradle files
3. Run on emulator or device

```bash
# Build the app
./gradlew assembleDebug

# Run tests
./gradlew test
```

### Backend Setup

1. Navigate to the backend directory:
```bash
cd backend
```

2. Install dependencies:
```bash
npm install
```

3. Copy environment file and configure:
```bash
cp .env.example .env
# Edit .env with your database credentials
```

4. Generate Prisma client:
```bash
npm run prisma:generate
```

5. Run database migrations:
```bash
npm run prisma:migrate
```

6. Seed the database:
```bash
npm run prisma:seed
```

7. Start the development server:
```bash
npm run start:dev
```

The API will be available at `http://localhost:3000/api`
Swagger docs at `http://localhost:3000/api/docs`

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/auth/register | Register new user |
| POST | /api/auth/login | Login user |
| GET | /api/auth/profile | Get user profile |
| GET | /api/venues | Search venues |
| GET | /api/venues/:id | Get venue details |
| GET | /api/venues/cities | Get available cities |
| GET | /api/packages | Get packages |
| GET | /api/packages/addons | Get addons |
| GET | /api/packages/time-slots | Get time slots |
| POST | /api/bookings | Create booking |
| GET | /api/bookings | Get user bookings |
| PATCH | /api/bookings/:id/cancel | Cancel booking |
| POST | /api/assistant/chat | Chat with AI assistant |

## Environment Variables

### Backend (.env)

| Variable | Description |
|----------|-------------|
| DATABASE_URL | PostgreSQL connection string |
| JWT_SECRET | Secret key for JWT tokens |
| JWT_EXPIRES_IN | Token expiration (e.g., "7d") |
| OPENROUTER_API_KEY | OpenRouter API key for AI |
| PORT | Server port (default: 3000) |

### Android (local.properties)

| Variable | Description |
|----------|-------------|
| API_BASE_URL | Backend API URL |

## Test Credentials

After seeding the database:

- **Admin**: admin@qa3at.com / admin123
- **User**: user@qa3at.com / user123

## Build Verification

### Android
```bash
./gradlew assembleDebug
./gradlew lint
```

### Backend
```bash
npm run build
npm run lint
npm run test
```

## License

MIT License - see LICENSE file for details.

## Support

For issues and feature requests, please open a GitHub issue.
