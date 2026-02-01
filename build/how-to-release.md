# How to release typescript-generator

- Create and publish a new GitHub release.
- TODO: This will trigger a GitHub actions run, deploying to Maven Central.
- Go to https://central.sonatype.com/publishing and promote the release.
    - "Staging Repositories"
    - "Close" the repo
    - wait for closing activities
    - "Release" the repo
- Wait for the release to appear in Maven Central - https://repo1.maven.org/maven2/com/github/kkuegler/typescript-generator/
- write release notes
- run "Release to Gradle plugin portal" GitHub Action
- close/update relevant issues and PRs
- remove unused tags
