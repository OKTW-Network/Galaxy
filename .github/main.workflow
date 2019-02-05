workflow "Workflow" {
  on = "push"
  resolves = ["Build"]
}

action "Build" {
  uses = "docker://openjdk:8-slim"
  runs = "./gradlew"
  args = "setupCiWorkspace build"
}
