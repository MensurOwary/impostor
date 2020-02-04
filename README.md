```
sudo docker build -t owary/impostor --build-arg config='./new_config.yml' . && sudo docker run -p 8888:8888 -it owary/impostor
```