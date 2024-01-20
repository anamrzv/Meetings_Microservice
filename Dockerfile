FROM rabbitmq:3.7.18-management
RUN rabbitmq-plugins enable rabbitmq_stomp
