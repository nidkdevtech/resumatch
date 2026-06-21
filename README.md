# ResuMatch 🎯
AI-powered job recommendation engine using RAG pipeline and vector similarity search.

## What it does
Upload your resume and get semantically matched job recommendations — not by keyword matching,
but by understanding the meaning of your experience and matching it against 10,000+ job listings.

## Tech Stack
- Java 21 + Spring Boot 3.5
- LangChain4j (RAG framework)
- PostgreSQL + pgvector (vector similarity search)
- Ollama / OpenAI (embeddings — provider agnostic)
- Groq LLaMA 3 (match explanations)
- React.js (frontend)
- Docker

## Dataset Setup
Download the LinkedIn Job Postings dataset from Kaggle:
https://www.kaggle.com/datasets/arshkon/linkedin-job-postings

Place `job_postings.csv` in:
`src/main/resources/data/job_postings.csv`

## How to Run
```bash
docker start job-rec-postgres
# then run ResuMatchApplication.java from IntelliJ
```

## Architecture
Coming soon.