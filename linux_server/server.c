#include <netinet/in.h> //for sockaddr_in
#include <sys/types.h>  // for socket
#include <sys/socket.h> // for socket
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define SECURITY_SERVER_PORT 6666
#define LENGTH_OF_LISTEN_QUEUE 20
#define BUFFER_SIZE 1024

int main(int argc, char **argv)
{
    /*
     *  Init Socket ip and port
     */
    struct sockaddr_in server_addr;
    bzero(&server_addr,sizeof(server_addr));
    server_addr.sin_family = AF_INET; // AF_INET is tcp.
    server_addr.sin_addr.s_addr = htons(INADDR_ANY);
    server_addr.sin_port = htons(SECURITY_SERVER_PORT);

    // AF is ADDRESS FAMILY, PF is Protocol family
    int server_socket = socket(PF_INET, SOCK_STREAM, 0);
    if(server_socket < 0)
    {
        printf("Create Socket Failed! \n");
        return 0;
    }

    /*allow lots of client to connect this server*/
    int opt = 1;
    setsockopt(server_socket, SOL_SOCKET, SO_REUSEADDR, &opt, sizeof(opt));

    if(bind(server_socket, (struct sockaddr*)&server_addr, sizeof(server_addr)))
    {
        printf("Server Bind Port : %d Failed! \n", SECURITY_SERVER_PORT);
        exit(1);
    }

    if(listen(server_socket, LENGTH_OF_LISTEN_QUEUE))
    {
        printf("Server Listen Failed!");
        exit(1);
    }

    /* Server need always run*/
    while(1)
    {
        struct sockaddr_in client_addr;
        socklen_t length = sizeof(client_addr);

        /*accept return new socket, this socket used to connect with client*/
        int new_server_socket = accept(server_socket, (struct sockaddr*)&client_addr, &length);
        if(new_server_socket < 0)
        {
            printf("Server Accept Failed! \n");
            break;
        }

        char buffer[BUFFER_SIZE];
        bzero(buffer, BUFFER_SIZE);
        length = recv(new_server_socket, buffer, BUFFER_SIZE, 0);
        if(length < 0)
        {
            printf("Server Receive Data Failed! \n");
        }

        // copy data to server buffer
        char data_from_client[BUFFER_SIZE];
        bzero(data_from_client, BUFFER_SIZE+1);
        strncpy(data_from_client, buffer, strlen(buffer)>BUFFER_SIZE?BUFFER_SIZE:strlen(buffer));

        printf("%s \n", data_from_client);

        // TODO: the data is right or error!
        if(data_from_client != NULL)
        {
            char result[] = "Right";
            int length = strlen(result);
            if(send(new_server_socket, result, length, 0) < 0)
            {
                printf("Send File Failed \n");
                close(new_server_socket);
                break;
            }
        }
        else 
        {
            printf("Result is null");
            close(new_server_socket);
            break; 
        }
        close(new_server_socket);
    }
    close(server_socket);
    return 0; 
}
