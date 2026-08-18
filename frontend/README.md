# Kanban Frontend

React + TypeScript frontend for the Kanban task management system.

## Prerequisites

- Node.js 18+ and npm
- Backend API running at `http://localhost:8080/api/v1`

## Quick Start

```bash
# Install dependencies
npm install

# Start development server
npm run dev

# Open http://localhost:5173
```

## Environment Variables

Create a `.env` file:

```env
VITE_API_URL=http://localhost:8080/api/v1
VITE_WS_URL=http://localhost:8080/api/v1/ws
```

## Features

✅ **Authentication**: JWT-based login/register with refresh token flow  
✅ **Dashboard**: Team overview with task counts  
✅ **Kanban Board**: Drag-and-drop task management across status columns  
✅ **Real-time Updates**: WebSocket integration for live collaboration  
✅ **Task Management**: Create, edit, assign, comment, attach files  
✅ **Team Management**: Create teams, manage members  
✅ **Analytics**: Charts for status distribution and workload  
✅ **Moderation**: Flag and resolve comments (moderator role)  
✅ **Responsive Design**: Works on desktop, tablet, and mobile  

## Tech Stack

- **React 18** + **TypeScript**
- **Vite** for build tooling
- **Tailwind CSS** for styling
- **React Router** for navigation
- **TanStack Query** (React Query) for server state
- **Zustand** for client state
- **@dnd-kit** for drag-and-drop
- **Axios** with JWT interceptors
- **STOMP/SockJS** for WebSocket
- **Recharts** for analytics charts
- **React Hook Form** + **Zod** for forms
- **Vitest** for testing

## Project Structure

```
src/
├── api/          # API client with interceptors
├── components/   # Reusable UI components
├── hooks/        # React Query hooks per resource
├── pages/        # Route pages
├── store/        # Zustand stores
├── types/        # TypeScript types from backend
└── utils/        # Helper functions
```

## Available Scripts

```bash
npm run dev      # Start dev server (port 5173)
npm run build    # Build for production
npm run preview  # Preview production build
npm run test     # Run tests
npm run lint     # Lint code
```

## Usage

### 1. Register/Login

Navigate to `/register` or `/login` to create an account or sign in.

### 2. Create a Team

From the dashboard, click "Create Team" to start a new team. You'll automatically become the team lead.

### 3. Create Tasks

Open a team board and click "New Task". Fill in the title, description, priority, and deadline.

### 4. Drag & Drop

Drag tasks between columns (To Do → In Progress → In Review → Done) to update their status. Changes sync in real-time to all team members.

### 5. Task Details

Click any task card to open the detail modal where you can:
- Edit task details
- Add comments
- Upload attachments
- Assign to team members

### 6. Analytics

Visit `/teams/:teamId/analytics` to see:
- Task status distribution (pie chart)
- Team workload (bar chart)
- Completion metrics

## Real-time Features

The app uses WebSocket (STOMP over SockJS) to provide real-time updates:
- Task created/updated/deleted
- Comments added/deleted
- Attachments added/deleted

All team members see changes instantly without refreshing.

## Development

### Adding New Features

1. **Add Types**: Update `src/types/index.ts`
2. **Create Hook**: Add React Query hook in `src/hooks/`
3. **Build Component**: Create component in `src/components/`
4. **Add Route**: Update `src/App.tsx`

### API Integration

All API calls go through `src/api/client.ts` which:
- Adds JWT token to requests
- Handles 401 with token refresh
- Redirects to login on auth failure

### State Management

- **Server State**: TanStack Query with automatic caching
- **Client State**: Zustand for auth and UI state
- **Form State**: React Hook Form with Zod validation

## Building for Production

```bash
# Build optimized bundle
npm run build

# Output in dist/ folder
# Serve with any static file server
```

## Deployment

### Option 1: Static Hosting (Vercel, Netlify)

```bash
npm run build
# Deploy dist/ folder
```

### Option 2: Docker

```dockerfile
FROM node:18-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=builder /app/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

## Environment Configuration

For production, update `.env`:

```env
VITE_API_URL=https://api.yourdomqin.com/api/v1
VITE_WS_URL=wss://api.yourdomain.com/api/v1/ws
```

## Troubleshooting

### API Connection Issues

Check that:
1. Backend is running on port 8080
2. CORS is configured correctly in backend
3. `.env` file exists with correct API_URL

### WebSocket Not Connecting

1. Verify WebSocket endpoint in backend
2. Check browser console for connection errors
3. Ensure SockJS endpoint is accessible

### Build Errors

```bash
# Clear cache and reinstall
rm -rf node_modules package-lock.json
npm install
```

## Testing

```bash
# Run unit tests
npm test

# Run with coverage
npm test -- --coverage

# Run in watch mode
npm test -- --watch
```

## License

Apache 2.0

## Support

For issues, check the backend API is running and accessible at the configured URL.
