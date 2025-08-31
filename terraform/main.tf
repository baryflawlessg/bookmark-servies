# Data sources to get default VPC and subnet
data "aws_vpc" "default" {
  count = var.vpc_id == "" ? 1 : 0
  default = true
}

data "aws_subnets" "default" {
  count = var.subnet_id == "" ? 1 : 0
  filter {
    name   = "vpc-id"
    values = [var.vpc_id != "" ? var.vpc_id : data.aws_vpc.default[0].id]
  }
}

# Create SSH key pair
resource "aws_key_pair" "bookverse_key" {
  key_name   = var.key_name
  public_key = file("${path.module}/ssh/bookverse-key.pub")
}

# Create security group
resource "aws_security_group" "bookverse_sg" {
  name_prefix = "${var.app_name}-${var.environment}-sg"
  description = "Security group for BookVerse application"

  vpc_id = var.vpc_id != "" ? var.vpc_id : data.aws_vpc.default[0].id

  # SSH access
  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
    description = "SSH access"
  }

  # HTTP access
  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
    description = "HTTP access"
  }

  # HTTPS access
  ingress {
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
    description = "HTTPS access"
  }

  # Application port (8080)
  ingress {
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
    description = "Application port"
  }

  # All outbound traffic
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
    description = "All outbound traffic"
  }

  tags = {
    Name        = "${var.app_name}-${var.environment}-sg"
    Environment = var.environment
    Application = var.app_name
  }
}

# Create EC2 instance
resource "aws_instance" "bookverse_server" {
  ami           = data.aws_ami.amazon_linux.id
  instance_type = var.instance_type
  key_name      = aws_key_pair.bookverse_key.key_name

  vpc_security_group_ids = [aws_security_group.bookverse_sg.id]
  subnet_id              = var.subnet_id != "" ? var.subnet_id : data.aws_subnets.default[0].ids[0]

  associate_public_ip_address = true

  user_data = base64encode(templatefile("${path.module}/user_data.sh", {
    app_name = var.app_name
  }))

  root_block_device {
    volume_size = 8
    volume_type = "gp2"
  }

  tags = {
    Name        = "${var.app_name}-${var.environment}-server"
    Environment = var.environment
    Application = var.app_name
  }
}

# Create Elastic IP
resource "aws_eip" "bookverse_eip" {
  instance = aws_instance.bookverse_server.id
  domain   = "vpc"

  tags = {
    Name        = "${var.app_name}-${var.environment}-eip"
    Environment = var.environment
    Application = var.app_name
  }
}

# Data source for Amazon Linux 2 AMI
data "aws_ami" "amazon_linux" {
  most_recent = true
  owners      = ["amazon"]

  filter {
    name   = "name"
    values = ["amzn2-ami-hvm-*-x86_64-gp2"]
  }

  filter {
    name   = "virtualization-type"
    values = ["hvm"]
  }
}
