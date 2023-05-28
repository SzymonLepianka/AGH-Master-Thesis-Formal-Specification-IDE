FROM ubuntu:latest

# Install dependencies (e.g., GCC, make)
RUN apt-get update && apt-get install -y build-essential

# Install nginx and configure it
RUN apt-get install -y nginx
RUN echo "daemon off;" >> /etc/nginx/nginx.conf

# PROVER9
  # Copy Prover9 tar.gz file into the Docker image
COPY LADR-2009-11A.tar.gz /tmp/LADR-2009-11A.tar.gz

  # Extract the contents of the tar.gz file
RUN tar -xzf /tmp/LADR-2009-11A.tar.gz -C /opt

  # Copy the replacement Makefile into the extracted directory
COPY Makefile /opt/LADR-2009-11A/provers.src/Makefile

  # Set the working directory
WORKDIR /opt/LADR-2009-11A

  # Compile Prover9, Mace4, and related programs
RUN make all

  # Run quick tests
RUN make test1
RUN make test2
RUN make test3

# SPASS
  # Copy the SPASS tar.gz file into the Docker image
COPY spass35pclinux64.tgz /tmp/spass35pclinux64.tgz

  # Extract the contents of the SPASS tar.gz file
RUN tar -xzf /tmp/spass35pclinux64.tgz -C /opt

  # Set the working directory
WORKDIR /opt/SPASS-3.5

  # Configure and compile SPASS
RUN ./configure && make

  # Install SPASS
RUN make install 

# Set the shared directory for file exchange
VOLUME /shared

# Expose port 80 for nginx
EXPOSE 80

# Start nginx as the command
CMD ["nginx"]