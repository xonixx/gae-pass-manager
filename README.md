# Personal Password Manager running on GAE

## Features
1. Super simple deploy into Google App Engine.
1. Fits nicely into free GAE quotas. You will have your personal cloud application
absolutely free of any charge.
1. Secure:
    1. Runs over HTTPS.
    1. Application is behind Google account authorisation form. It means the login
      process is two-step - first you login with Google account, then you login into app
      with master-password.
    1. Client-side encryption (AES-256). All passwords data is encrypted/decrypted
     in browser using [sjcl](https://crypto.stanford.edu/sjcl/) js lib. No un-encrypted data is sent to Google servers.
    1. You totally manage the whole setup by your own. Source code is open.
1. Allows to download all passwords data in form of self-contained read-only
master-password-protected HTML page.
1. Automatically logs you out after 5 minutes of inactivity.

## Installation
1. You need to have installed Java 7, Maven 3.
1. Create new GAE application. This gives you personal _https://your-app-id.appspot.com_ website.
1. Checkout the code.
1. Run `mvn appengine:update -Dappid=your-app-id`
1. Start using the app!
