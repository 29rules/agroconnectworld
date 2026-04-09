# Chatbot Testing Checklist

## ✅ Build Status
- [x] Component compiles without errors
- [x] No linting errors
- [x] CSS file exists and is imported

## 🔍 Component Structure Tests

### 1. Component Imports
- [x] React hooks (useState, useRef, useEffect)
- [x] AuthContext (useAuth)
- [x] ToastContext (useToast)
- [x] API service (api)

### 2. State Management
- [x] isOpen state for chat window
- [x] messages array for chat history
- [x] input state for user input
- [x] loading state for API calls
- [x] schedulingAppointment state
- [x] appointmentData object

### 3. Features to Test

#### A. Chat Window Toggle
- [ ] Floating button appears on page
- [ ] Button toggles chat window open/close
- [ ] Close button works
- [ ] Notification badge appears when messages exist

#### B. Welcome Message
- [ ] Welcome message appears when chat opens
- [ ] Message only appears once per session

#### C. Message Sending
- [ ] User can type messages
- [ ] Enter key sends message
- [ ] Send button works
- [ ] Input clears after sending
- [ ] Loading indicator shows during API call

#### D. OpenRouter API Integration
- [ ] API key is read from environment variable
- [ ] Correct API endpoint is called
- [ ] Request includes system prompt
- [ ] Request includes conversation history
- [ ] Response is parsed correctly
- [ ] Error handling works for API failures

#### E. Appointment Scheduling
- [ ] Form appears when user mentions "schedule" or "appointment"
- [ ] Form fields are present (name, email, phone, date, time, reason)
- [ ] Form validation works
- [ ] Submission calls contact API
- [ ] Success message appears
- [ ] Form resets after submission

#### F. User Authentication Integration
- [ ] Pre-fills user data if logged in
- [ ] Works for both authenticated and unauthenticated users

#### G. UI/UX
- [ ] Messages scroll to bottom automatically
- [ ] Typing indicator shows during loading
- [ ] Mobile responsive
- [ ] Styling matches design system

## 🧪 Manual Testing Steps

1. **Start the development server:**
   ```bash
   cd frontend
   npm run dev
   ```

2. **Test Chat Window:**
   - Open browser to http://localhost:5173
   - Look for floating chat button (bottom-right)
   - Click to open chat window
   - Verify welcome message appears

3. **Test Message Sending:**
   - Type a message: "What products do you offer?"
   - Press Enter or click Send
   - Verify message appears in chat
   - Verify loading indicator shows
   - Wait for response (requires API key)

4. **Test Appointment Scheduling:**
   - Type: "I want to schedule an appointment"
   - Verify form appears
   - Fill in form fields
   - Submit form
   - Verify success message

5. **Test Error Handling:**
   - Without API key, send a message
   - Verify error message appears
   - Verify user-friendly error message

## ⚠️ Known Requirements

1. **Environment Variable:**
   - Must set `VITE_OPENROUTER_API_KEY` in `.env` file
   - Without it, chatbot will show error message

2. **Backend API:**
   - Contact API endpoint must be available at `/api/contact`
   - Must accept POST requests with appointment data

## 🐛 Potential Issues to Check

1. **API Key Missing:**
   - Error: "The chatbot service is not properly configured"
   - Solution: Add `VITE_OPENROUTER_API_KEY` to `.env`

2. **CORS Issues:**
   - Error: Network error or CORS blocked
   - Solution: OpenRouter API should handle CORS, but check if needed

3. **Contact API Not Available:**
   - Error: Failed to submit appointment request
   - Solution: Ensure backend contact service is running

4. **CSS Not Loading:**
   - Chatbot styles not applied
   - Solution: Verify `Chatbot.css` is imported in `App.jsx`



