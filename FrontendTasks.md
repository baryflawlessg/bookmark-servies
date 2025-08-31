# Frontend Tasks by Page (MVP)

## Global/App Shell
- Set up routing with protected/public routes
- Implement top navigation with links: Books, Recommendations, Profile, Login/Signup/Logout
- Configure auth context/provider with JWT in localStorage and axios interceptors
- Global UI states: loading spinner, error toasts, empty states
- Responsive layout and base Tailwind styles
- Jest, React Testing Library

## Auth Pages
- Login page
  - Email/password form with validation
  - Submit to login API; persist JWT; redirect to Books
  - Error handling and inline messages
- Signup page
  - Name/email/password form with validation
  - Submit to signup API; success redirect to Login
  - Error handling (duplicate email, weak password)
- Logout action
  - Clear token/state; redirect to home

## Books Listing Page
- Grid/list view toggle
- Search bar (title, author, genre)
- Filter dropdowns (genre, rating, year)
- Sort options (title, author, rating, date)
- Pagination
- Book cards with cover, title, author, rating
- Loading states and empty states
- Responsive design (mobile-friendly)

## Book Details Page
- Book information display (cover, title, author, description, etc.)
- Average rating display with star visualization
- User's personal rating
- Review form (rating + text)
- Reviews list with pagination
- Review actions (edit/delete for own reviews)
- Loading states and error handling

## User Profile Page
- User information display
- Review history with pagination
- Favorite books list
- Loading states and form validation

## Recommendations Page
- Personalized book recommendations grid. Top rated books.
- Recommendation of books based on genres of favourites
- Loading states and empty states


## Global Components
- LoadingSpinner (reusable)
- ErrorBoundary for error handling
- Toast notifications for user feedback
- Modal components for confirmations
- Form components with validation
- Pagination component
- Search component
- Filter/Sort components

## Testing Tasks
- Unit tests for all components
- Mock API responses for testing
- Test coverage reporting

## Styling & UX
- Consistent design system with Tailwind
- Accessibility compliance (ARIA labels, keyboard navigation)
- Mobile-first responsive design
- Smooth transitions and animations
- Error states and empty states

## Performance & Optimization
- Code splitting for routes
- Lazy loading for images
- Memoization for expensive components
- Bundle size optimization
- Caching strategies for API responses
