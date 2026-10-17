# John AI APK Maker

Phone-friendly Android starter project with a dark neon UI, idea input, OpenAI-powered project planning through a separate backend, and a GitHub Actions workflow that builds this Android app into a debug APK.

## Build the APK on GitHub using your phone
1. Create a GitHub repository named `John-AI-APK-Maker`.
2. Upload all files/folders in this ZIP to the repository root. Keep the exact paths.
3. Open **Actions** → **Build John AI APK Maker** → **Run workflow**.
4. When the run finishes, open it and download the `John-AI-APK-Maker` artifact.
5. Extract the downloaded artifact ZIP on your phone to get the APK.

## Connect OpenAI
1. Deploy the `backend` folder to a Node.js host that supports environment variables and HTTPS.
2. Set `OPENAI_API_KEY` as a secret/environment variable on that host. Do not place it in Android source code or GitHub commits.
3. Set `OPENAI_MODEL` if needed to a model enabled for your API account.
4. In the app, replace the example backend URL with your HTTPS backend URL ending in `/generate`.

## Honest current scope
This first version builds a real APK for **John AI APK Maker itself**. Its AI feature returns an Android project plan and starter code guidance. It does **not yet compile arbitrary ideas into new APKs**. That requires a separate isolated build service and APK download/artifact flow. The UI and backend are a foundation for that next step.

OpenAI API usage may cost money and is billed separately from a ChatGPT subscription.
