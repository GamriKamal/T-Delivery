COLOR_BLUE := \033[1;94m
COLOR_RESET := \033[0m

LABEL := $(shell hostname)

ROOT_DIR := $(shell find $(CURDIR) -type d -name "T-Delivery" -print -quit)

ifeq ($(ROOT_DIR),)
$(error "$(COLOR_BLUE)[$(LABEL)] T-Delivery directory not found. Make sure it exists.$(COLOR_RESET)")
endif

SERVICES := auth-service courier-service discovery-service gateway-service menu-service message-service order-service route-service user-service
SERVICE_PATHS := $(addprefix $(ROOT_DIR)/, $(SERVICES))

MAVEN_BUILD_CMD := mvn clean package
GRADLE_BUILD_CMD := ./gradlew clean build --console=plain --info

DOCKER_COMPOSE := docker-compose

.PHONY: build build-all docker-up docker-down

build:
	@if [ "$(name)" = "" ]; then \
		echo "$(COLOR_BLUE)[$(LABEL)] Please specify the service name with 'name=<service-name>'$(COLOR_RESET)"; \
	else \
		if [ -d $(ROOT_DIR)/$(name) ]; then \
			echo "$(COLOR_BLUE)[$(LABEL)] Building $(name)$(COLOR_RESET)"; \
			sleep 0.2; \
			if [ -f $(ROOT_DIR)/$(name)/pom.xml ]; then \
				script -q -c "cd $(ROOT_DIR)/$(name) && $(MAVEN_BUILD_CMD)" /dev/null || { \
					echo "$(COLOR_BLUE)[$(LABEL)] Build failed for $(name). Stopping.$(COLOR_RESET)"; exit 1; }; \
			elif [ -f $(ROOT_DIR)/$(name)/build.gradle.kts ] || [ -f $(ROOT_DIR)/$(name)/build.gradle ]; then \
				script -q -c "cd $(ROOT_DIR)/$(name) && $(GRADLE_BUILD_CMD)" /dev/null || { \
					echo "$(COLOR_BLUE)[$(LABEL)] Build failed for $(name). Stopping.$(COLOR_RESET)"; exit 1; }; \
			else \
				echo "$(COLOR_BLUE)[$(LABEL)] Build file not found for $(name). Skipping.$(COLOR_RESET)"; \
			fi; \
			echo ""; echo ""; \
			sleep 10; \
		else \
			echo "$(COLOR_BLUE)[$(LABEL)] Service $(name) not found.$(COLOR_RESET)"; \
		fi; \
	fi

build-all:
	@echo "$(COLOR_BLUE)[$(LABEL)] Building all services...$(COLOR_RESET)"
	@for service in $(SERVICES); do \
		echo "$(COLOR_BLUE)[$(LABEL)] Building $$service$(COLOR_RESET)"; \
		sleep 0.2; \
		if [ -f $(ROOT_DIR)/$$service/pom.xml ]; then \
			script -q -c "cd $(ROOT_DIR)/$$service && $(MAVEN_BUILD_CMD)" /dev/null || { \
				echo "$(COLOR_BLUE)[$(LABEL)] Build failed for $$service. Stopping.$(COLOR_RESET)"; exit 1; }; \
		elif [ -f $(ROOT_DIR)/$$service/build.gradle.kts ] || [ -f $(ROOT_DIR)/$$service/build.gradle ]; then \
			script -q -c "cd $(ROOT_DIR)/$$service && $(GRADLE_BUILD_CMD)" /dev/null || { \
				echo "$(COLOR_BLUE)[$(LABEL)] Build failed for $$service. Stopping.$(COLOR_RESET)"; exit 1; }; \
		else \
			echo "$(COLOR_BLUE)[$(LABEL)] Build file not found for $$service. Skipping.$(COLOR_RESET)"; \
		fi; \
		echo ""; echo ""; \
		sleep 10; \
	done
	@echo "$(COLOR_BLUE)[$(LABEL)] All services built successfully.$(COLOR_RESET)"

docker-up:
	@echo "$(COLOR_BLUE)[$(LABEL)] Starting database and Kafka containers...$(COLOR_RESET)"
	cd $(ROOT_DIR) && $(DOCKER_COMPOSE) up -d zookeeper kafka user-service-postgresql_db order-service-postgresql_db route-service-postgresql_db menu-service-mongodb
	sleep 20
	@echo "$(COLOR_BLUE)[$(LABEL)] Starting all other containers...$(COLOR_RESET)"
	cd $(ROOT_DIR) && $(DOCKER_COMPOSE) up -d

docker-down:
	@echo "$(COLOR_BLUE)[$(LABEL)] Stopping all containers...$(COLOR_RESET)"
	cd $(ROOT_DIR) && $(DOCKER_COMPOSE) down
