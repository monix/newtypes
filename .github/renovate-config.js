module.exports = {
  platform: "github",
  repositories: ["monix/newtypes"],
  gitAuthor: "Renovate Bot <renovate@monix.org>",
  branchPrefix: "renovate/",
  onboarding: false,
  requireConfig: "optional",
  recreateWhen: "always",
  prHourlyLimit: 0,

  extends: [":dependencyDashboard"],

  enabledManagers: ["github-actions", "sbt"],

  ignorePaths: ["**/.gradle/**"],

  packageRules: [
    {
      description: "Group all dependency updates into a single PR",
      matchManagers: ["github-actions", "sbt"],
      groupName: "dependencies",
      groupSlug: "all-dependencies",
      group: {
        commitMessageTopic: "dependencies",
        commitMessageExtra: "",
      },
    },
    {
      description: "Only use stable dotted numeric JVM dependency versions",
      matchManagers: ["sbt"],
      allowedVersions: "/^\\d+(?:\\.\\d+)+$/",
    },
    {
      description: "Keep Scala on the 3.3.x line",
      matchManagers: ["sbt"],
      matchPackageNames: ["org.scala-lang:scala3-library_3"],
      allowedVersions: "/^3\\.3\\.\\d+$/",
    },
    {
      description: "Wait one week before proposing dependency updates",
      matchManagers: ["github-actions", "sbt"],
      minimumReleaseAge: "7 days",
      minimumReleaseAgeBehaviour: "timestamp-optional",
    },
  ],
};
