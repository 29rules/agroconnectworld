# Chatbot Setup Guide

## Issue
The chatbot is showing connection errors because the OpenRouter API key is not configured.

## Solution

### Step 1: Get Your OpenRouter API Key

1. Go to [OpenRouter.ai](https://openrouter.ai/)
2. Sign up or log in
3. Navigate to [API Keys](https://openrouter.ai/keys)
4. Create a new API key
5. Copy the API key (it will look like: `sk-or-v1-...`)

### Step 2: Create Environment File

1. Navigate to the `frontend` directory:
   ```bash
   cd frontend
   ```

2. Create a `.env` file:
   ```bash
   touch .env
   ```

3. Add your API key to the `.env` file:
   ```env
   VITE_OPENROUTER_API_KEY=sk-or-v1-your-actual-api-key-here
   ```

   **Important:** Replace `sk-or-v1-your-actual-api-key-here` with your actual API key from OpenRouter.

### Step 3: Restart Development Server

1. Stop the current development server (press `Ctrl+C` in the terminal)
2. Start it again:
   ```bash
   npm run dev
   ```

### Step 4: Test the Chatbot

1. Open your browser to `http://localhost:5173`
2. Click the floating chat button (bottom-right corner)
3. Try sending a message like "What products do you offer?"
4. The chatbot should now respond correctly!

## Troubleshooting

### Error: "API key not configured"
- Make sure the `.env` file is in the `frontend` directory (not the root)
- Make sure the variable name is exactly: `VITE_OPENROUTER_API_KEY`
- Make sure there are no spaces around the `=` sign
- Restart the dev server after creating/modifying `.env`

### Error: "API key invalid"
- Verify your API key is correct
- Check if your OpenRouter account has credits
- Make sure you copied the entire key (they're usually long)

### Still Not Working?
- Check the browser console (F12) for detailed error messages
- Verify the `.env` file is not in `.gitignore` (it should be ignored for security)
- Make sure you're using Vite's environment variable prefix `VITE_`

## Security Note

⚠️ **Never commit your `.env` file to git!** It should already be in `.gitignore`. Your API key is sensitive and should be kept private.

## Alternative: Test Without API Key

If you want to test the chatbot UI without an API key, the chatbot will show helpful error messages explaining how to configure it. The appointment scheduling form will still work even without the API key.



