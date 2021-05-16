# Use with Docker
## Build

Build the base-images first. The final image depends on them:

```shell
./docker/build_base_images.sh
```

Once the base-images are built, you can build the final image. You don't need to rebuild the base images if they are already present in your local repository.

```shell
docker build -t dms:dev .
```

## Run

**Note:** this will be refractored in the future, since not all project-files are required to run the DMS

```shell
docker run --volume [full path to your project root]:/home/gradle/src/ -w /home/gradle/src/ -p 8888:8888 dms:dev
```