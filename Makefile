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
GRADLE_BUILD_CMD := ./gradlew clean build

DOCKER_COMPOSE := docker-compose

LOG_DIR := $(CURDIR)/tdelivery_logs

$(shell mkdir -p $(LOG_DIR))

.PHONY: build build-all docker-up docker-down

build:
	@if [ "$(name)" = "" ]; then \
		echo "$(COLOR_BLUE)[$(LABEL)] Please specify the service name with 'name=<service-name>'$(COLOR_RESET)"; \
	else \
		if [ -d $(ROOT_DIR)/$(name) ]; then \
			echo "$(COLOR_BLUE)[$(LABEL)] Building $(name)$(COLOR_RESET)"; \
			rm -f $(LOG_DIR)/$(name)_build.log; \
			sleep 0.2; \
			if [ -f $(ROOT_DIR)/$(name)/pom.xml ]; then \
				cd $(ROOT_DIR)/$(name) && $(MAVEN_BUILD_CMD) > $(LOG_DIR)/$(name)_build.log 2>&1; \
			elif [ -f $(ROOT_DIR)/$(name)/build.gradle.kts ] || [ -f $(ROOT_DIR)/$(name)/build.gradle ]; then \
				cd $(ROOT_DIR)/$(name) && $(GRADLE_BUILD_CMD) > $(LOG_DIR)/$(name)_build.log 2>&1; \
			else \
				echo "$(COLOR_BLUE)[$(LABEL)] Build file not found for $(name). Skipping.$(COLOR_RESET)"; \
			fi; \
		else \
			echo "$(COLOR_BLUE)[$(LABEL)] Service $(name) not found.$(COLOR_RESET)"; \
		fi; \
	fi

build-all:
	@echo "$(COLOR_BLUE)[$(LABEL)] Building all services...$(COLOR_RESET)"
	rm -f $(LOG_DIR)/*_build.log
	@for service in $(SERVICES); do \
		echo ""; \
		echo "$(COLOR_BLUE)[$(LABEL)] Building $$service$(COLOR_RESET)"; \
		sleep 0.2; \
		if [ -f $(ROOT_DIR)/$$service/pom.xml ]; then \
			cd $(ROOT_DIR)/$$service && $(MAVEN_BUILD_CMD) > $(LOG_DIR)/$$service_build.log 2>&1; \
		elif [ -f $(ROOT_DIR)/$$service/build.gradle.kts ] || [ -f $(ROOT_DIR)/$$service/build.gradle ]; then \
			cd $(ROOT_DIR)/$$service && $(GRADLE_BUILD_CMD) > $(LOG_DIR)/$$service_build.log 2>&1; \
		else \
			echo "$(COLOR_BLUE)[$(LABEL)] Build file not found for $$service. Skipping.$(COLOR_RESET)"; \
		fi; \
	done
	@echo "$(COLOR_BLUE)[$(LABEL)] All services built successfully.$(COLOR_RESET)"
	@echo "$(COLOR_BLUE)[$(LABEL)] You can view the logs in the following folder: $(LOG_DIR)/$(COLOR_RESET)"
	@echo "$(COLOR_BLUE)[$(LABEL)] Click here: file://$(LOG_DIR)/$(COLOR_RESET)"

docker-up:
	@echo "$(COLOR_BLUE)[$(LABEL)] Starting database and Kafka containers...$(COLOR_RESET)"
	cd $(ROOT_DIR) && $(DOCKER_COMPOSE) up -d zookeeper kafka user-service-postgresql_db order-service-postgresql_db route-service-postgresql_db menu-service-mongodb
	sleep 20
	@echo "$(COLOR_BLUE)[$(LABEL)] Starting all other containers...$(COLOR_RESET)"
	cd $(ROOT_DIR) && $(DOCKER_COMPOSE) up -d

docker-down:
	@echo "$(COLOR_BLUE)[$(LABEL)] Stopping all containers...$(COLOR_RESET)"
	cd $(ROOT_DIR) && $(DOCKER_COMPOSE) down
