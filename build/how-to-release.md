# How to release typescript-generator

- Create and publish a new GitHub release.
- This will trigger a GitHub actions run, deploying to Maven Central.
  - The version number is determined by dropping 'v' from the tag name like 'v4.3.2'
  - Also works with pre-releases and 'v4.0.0-SNAPSHOT' style tag names.
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
