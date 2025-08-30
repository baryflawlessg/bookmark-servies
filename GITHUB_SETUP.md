# GitHub Setup Guide

## Required GitHub Secrets

You need to add these secrets to your GitHub repository:

### 1. AWS Credentials
- **AWS_ACCESS_KEY_ID**: Your AWS access key
- **AWS_SECRET_ACCESS_KEY**: Your AWS secret key  
- **AWS_REGION**: Your AWS region (e.g., us-east-1)

### 2. EC2 Connection Details
- **EC2_HOST**: Your EC2 public IP (3.222.118.248)
- **EC2_SSH_KEY**: Your private SSH key content

## How to Add Secrets

1. **Go to your GitHub repository**
2. **Click Settings** tab
3. **Click Secrets and variables** → **Actions**
4. **Click New repository secret**
5. **Add each secret** with the values above

## Getting the SSH Key

Copy the content of your private key file:
```bash
cat terraform/ssh/bookverse-key
```

Copy the entire output (including `-----BEGIN OPENSSH PRIVATE KEY-----` and `-----END OPENSSH PRIVATE KEY-----`)

## Workflow Usage

### Manual Infrastructure Management
1. **Go to Actions** tab
2. **Select "Infrastructure Management"**
3. **Click "Run workflow"**
4. **Choose action**: plan, apply, or destroy

### Automatic Deployment
- **Push to main branch** → Triggers automatic deployment
- **Manual trigger** → Go to Actions → "Build and Deploy BookVerse" → "Run workflow"

## Testing the Setup

After setting up secrets:
1. **Push a change** to main branch
2. **Check Actions** tab for deployment progress
3. **Test your application**: http://3.222.118.248:8080
