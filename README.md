# Impostor - Mock HTTP Server
To build and run the application:

`sudo docker build -t impostor --build-arg config='./config.yml' . && sudo docker run -p 8888:8888 -it impostor`

[**config.yml**](./config.yml) file is the configuration file template.

For each endpoint the following fields are available so far:

- *path* - obviously the path of the endpoint
    
    - the path variables can be used inside the payload
    - {path_variable_name:data_type} 
        - `path_variable_name` can be used inside the payload using `${path_variable_name}` construct.
        - `data_type` so far can be either `number`, `string`
- *contentType* - contentType of the payload returned
- *method* - http method expected on that endpoint
- *payload* - what should be returned

