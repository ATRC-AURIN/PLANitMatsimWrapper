# This test script simulates the steps that the
# workflow runner will perform when running a 
# particular operation in Windows powershell 

# Build the container image: (this can be commented out for speedier test runs if the image is current)
docker build -t matsimwrapper-atrc:latest -f ./atrc/Dockerfile .

# Create folders for data input and output within ATRC sub dir:
mkdir -Force ./atrc/data
mkdir -Force ./atrc/data/inputs
mkdir -Force ./atrc/data/outputs

# Copy over test parameters and files:
cp ./src/test/resources/Melbourne/* ./atrc/data/inputs/
cp ./atrc/test_parameters.yaml ./atrc/data/parameters.yaml

# Run the container:
docker run --mount type=bind,source=${PWD}/atrc/data,target=/data matsimwrapper-atrc:latest
    