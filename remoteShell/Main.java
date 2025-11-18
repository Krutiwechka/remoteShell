package remoteShell;

import java.util.StringTokenizer;

public class Main {
	public static void main(String[] args) {
		try{
			if(args.length >= 1) {
				int i = 0;
				while(true) {
					if(args[i].equals("-h") || args[0].equals("--help")){
					System.out.println(
							"\t-l [PORT] 						Server starts listening chosen port\n" + 
							"\t-c, --connect [IP] [PORT]	 	Сonnect chosen server\n" +
							"\t-u, --udp 						Use UDP instead of default TCP\n" +
							"\t-h, --help 						Сommand line syntax\n");
					break;
					}
				else if(args[i].equals("-u" ) || args[i].equals("-udp" )){
					i++;
					
				}
			}
		}
	}

}
