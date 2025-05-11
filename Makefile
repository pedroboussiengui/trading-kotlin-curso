.PHONY: test

test:
	@gradle test --console=auto --rerun-tasks

quick-test:
	@gradle test