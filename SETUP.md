# Repository automation setup

Renovate and OpenCode authenticate as the `Monix Bot` GitHub App so
their pull requests, comments, commits, and pushes are not attributed to a
personal account.

## GitHub App

Create an organization-owned GitHub App at:

<https://github.com/organizations/monix/settings/apps/new>

Use these settings:

- Disable webhooks.
- Allow installation only on the owning account.
- Install the app only on `monix/newtypes`.
- Grant `Members: read-only` as an organization permission.
- Grant these repository permissions:
  - `Administration: read-only`
  - `Checks: read and write`
  - `Commit statuses: read and write`
  - `Contents: read and write`
  - `Dependabot alerts: read-only`
  - `Issues: read and write`
  - `Pull requests: read and write`
  - `Workflows: read and write`
  - `Metadata: read-only`, which GitHub grants automatically

`Contents: read and write` permits Git pushes. `Workflows: read and write` is
also required when a commit changes files under `.github/workflows`.

After creating the app, generate a private key and configure these repository
Actions values under **Settings > Secrets and variables > Actions**:

- Secret `AUTOMATION_APP_ID`: the GitHub App ID
- Secret `AUTOMATION_APP_PRIVATE_KEY`: the complete downloaded PEM private key,
  including its `BEGIN` and `END` lines

The workflows exchange these values for a repository-scoped installation token
using `actions/create-github-app-token`. The token expires after one hour and
the action revokes it when the job finishes.
