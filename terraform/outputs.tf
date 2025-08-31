output "instance_id" {
  description = "ID of the EC2 instance"
  value       = aws_instance.bookverse_server.id
}

output "public_ip" {
  description = "Public IP address of the EC2 instance"
  value       = aws_eip.bookverse_eip.public_ip
}

output "public_dns" {
  description = "Public DNS name of the EC2 instance"
  value       = aws_instance.bookverse_server.public_dns
}

output "security_group_id" {
  description = "ID of the security group"
  value       = aws_security_group.bookverse_sg.id
}

output "key_name" {
  description = "Name of the SSH key pair"
  value       = aws_key_pair.bookverse_key.key_name
}

output "ssh_command" {
  description = "SSH command to connect to the instance"
  value       = "ssh -i ssh/bookverse-key ec2-user@${aws_eip.bookverse_eip.public_ip}"
}

output "application_url" {
  description = "URL to access the application"
  value       = "http://${aws_eip.bookverse_eip.public_ip}:8080"
}
