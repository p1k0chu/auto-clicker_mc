GW := "./gradlew"
GWFLAGS := ""
gw := f"{{GW}} {{GWFLAGS}}"

assemble project="": (task "assemble" project)

switch project: (task f"Set active project to {{project}}")
reset: (task "Reset active project")
refresh: (task "Refresh active project")

buildAndCollect: (task "buildAndCollect")

clean: (task "clean")
	rm -rf build/libs

projects:
	./gradlew :projects

[script("bash")]
task task project="":
	if [[ -z "{{project}}" ]] then
		{{gw}} "{{task}}"
	else
		{{gw}} ":{{project}}:{{task}}"
	fi
