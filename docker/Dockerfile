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

## InKreSAT
#  # Install zlib development package
#RUN apt-get install -y zlib1g-dev
#RUN apt-get install -y ocaml 
#RUN apt-get install -y ocaml-findlib 
#RUN apt-get install -y libextlib-ocaml-dev
#RUN apt-get install -y minisat
#
#  # Copy the InKreSAT tar.bz2 file into the Docker image
##COPY inkresat-1.0.tar.bz2 /tmp/inkresat-1.0.tar.bz2
#COPY inkresat-zmiany3.tar /tmp/inkresat-zmiany3.tar
#
#  # Extract the contents of the InKreSAT tar.bz2 file
##RUN tar -xjf /tmp/inkresat-1.0.tar.bz2 -C /opt
#RUN tar -xvf /tmp/inkresat-zmiany3.tar -C /opt
#
#  # Set the working directory
#WORKDIR /opt/inkresat
#
#  # Build InKreSAT
#RUN make inkresat 

# Set the shared directory for file exchange
VOLUME /shared

# Expose port 80 for nginx
EXPOSE 80

# Start nginx as the command
CMD ["nginx"]