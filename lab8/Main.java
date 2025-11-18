package lab8;

import java.util.StringTokenizer;
/**
 * <p>Command processor base class
 * @author Timojj
 * @version 1.0
 */
public class Main {

	public static void main(String[] args) {
		try{
			if(args.length >= 1) {
				while(true) {
					
					if(args[0].equals("-h") || args[0].equals("--help")){
				}
					System.out.println(
							"\t-l [PORT] 		server starts listening chosen port\n" + 
							"\t-c, --connect 	[IP] [PORT] - connect chosen server\n" +
							"\t-u, --udp 		use UDP" +
							"\t-h, --help 		command line syntax\n");
					}
				else if(args[0].equals("-l" )){
					
				}
			}
		}
	}

}
