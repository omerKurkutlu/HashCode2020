package main;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class main {
    public static void main(String[] args) throws IOException {
        File file = new File("./src/files/b.txt");
        String cozum = 					 "b_output.txt";
		int limit=4;
        boolean yazdir = false;
        List<String> liste = new ArrayList<String>();
        int simTime=0, numInter=0, numStreet=0, numCar=0, point=0;

        BufferedReader br = new BufferedReader(new FileReader(file));

        String line;
        
        line = br.readLine();
        String[] l = line.split(" ");
        simTime = Integer.parseInt(l[0]);
        numInter = Integer.parseInt(l[1]);
        numStreet = Integer.parseInt(l[2]);
        numCar = Integer.parseInt(l[3]);
        point = Integer.parseInt(l[4]);
        List<Street> streets = new ArrayList<Street>();
        List<Car> cars = new ArrayList<Car>();
        List<Intersection> intersections = new ArrayList<Intersection>();
        Street s;
        Car c;
        Intersection inter;
        for(int i = 0 ;i <numInter;i++) {
        	inter = new Intersection();
        	intersections.add(i, inter);
        }
        int count = 0;
        
		while ((line = br.readLine()) != null) {
        	l=line.split(" ");
        	if(count<numStreet) {
        		//cadde ekleme
            	s = new Street();
            	s.begin=Integer.parseInt(l[0]);
            	s.end=Integer.parseInt(l[1]);
            	s.name=l[2];
            	s.time=Integer.parseInt(l[3]);
            	streets.add(s);
            	intersections.get(s.begin).outgoing.add(s);
            	intersections.get(s.end).income.add(s);
        	}else {
        		//araba ekleme
        		int size = Integer.parseInt(l[0]);
        		c  = new Car(size);
        		for(String k:l) {
        			if (!k.equals(l[0])) {
        				Street street1 = getStreetForStreetByName(streets, k);
        				//arabaya rota ekleme
        				street1.gecenAracSayisi++;
        				c.addStreet(k,street1.time);
        				
        			}
        		}
        		cars.add(c);
        	}
        	count++;
        }
		
		
///////////////////////// precessing////////////////////////////////////
		
		List<String> lines = new ArrayList<String>();
		line="";
		count=0;
		Street cadde ;
		String caddeAdi ;

		for(Intersection in:intersections) {
			int size=in.income.size();
        	if(size==1) {
        		in.income.get(0).pass = true;
        		lines.add(String.valueOf(intersections.indexOf(in)));
        		lines.add(String.valueOf(size));
        		int saniye=2;
        		for(int i =0;i<size;i++) {
            		lines.add(in.income.get(i).name+ " "+ String.valueOf(saniye));
        		}
        		count++;
        	}
        	else if(size==2) {
        		in.income.get(0).pass = true;
        		int saniye=1;
        		int n1=in.income.get(0).gecenAracSayisi;
        		int n2=in.income.get(1).gecenAracSayisi;
        		int obeb = ebob(n1,n2);
        		if(n1!=0 && n2!=0) {
            		lines.add(String.valueOf(intersections.indexOf(in)));
            		lines.add(String.valueOf(size));
        			cadde = in.income.get(0);
        			caddeAdi = cadde.name;
            		lines.add(caddeAdi + " "+ String.valueOf(cadde.gecenAracSayisi/obeb));
        			cadde = in.income.get(1);
        			caddeAdi = cadde.name;
            		lines.add(caddeAdi + " "+ String.valueOf(cadde.gecenAracSayisi/obeb));
            		count++;       
        		}else {
            		lines.add(String.valueOf(intersections.indexOf(in)));
            		lines.add(String.valueOf(size-1));
            		if(n1!=0) {
                		cadde = in.income.get(0);
            			caddeAdi = cadde.name;
            			saniye = (cadde.gecenAracSayisi/obeb)%simTime;
            			saniye = saniye==0? 1:saniye;
            			if(saniye>limit)
            				saniye = limit;
                		lines.add(caddeAdi + " "+ String.valueOf(saniye));            			
            		}
            		else {
            			cadde = in.income.get(1);
            			caddeAdi = cadde.name;
            			saniye = (cadde.gecenAracSayisi/obeb)%simTime;
            			saniye = saniye==0? 1:saniye;
            			if(saniye>limit)
            				saniye = limit;
                		lines.add(caddeAdi + " "+ String.valueOf(saniye));            			
            		}
            		count++;       
        			
        		}
 		
        	}else {
        		in.income.get(0).pass = true;
        		lines.add(String.valueOf(intersections.indexOf(in)));
        		lines.add(String.valueOf(size));
        		int saniye=1;
        		for(int i =0;i<size;i++) {
            		lines.add(in.income.get(i).name+ " "+ String.valueOf(saniye));
        		}
        		count++;        		        		
        	}
        }
		lines.add(0, String.valueOf(count));
        whenWriteStringUsingBufferedWritter_thenCorrect(cozum,lines);
        if(yazdir) {
			for(Car car:cars) {
				System.out.println(car);
			}
			for(Street street:streets) {
				System.out.println(street);
			}        	
        }
    }

    public static Street getStreetForStreetByName(List<Street> list,String name) {
    	for(Street s:list) {
    		if(s.name.equals(name))
    			return s;
    	}
    	
    	return null;
    }
    public static void whenWriteStringUsingBufferedWritter_thenCorrect(String fileName, List<String> lines)
            throws IOException {
        fileName = "./src/output/"+fileName;
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        for (String line:lines)	
        	writer.write(line+"\n");
        writer.close();
    }
    public static class Street {
    	String name;
    	int begin;
    	int end;
    	int gecenAracSayisi;
    	int time;
    	boolean pass;
    	public Street() {
    		this.gecenAracSayisi=0;
    		this.pass=false;
    	}
    	@Override
    	public String toString() {
    		return name + " " + begin + " --> " + end + "(" + time + " saniye) | gecen aracSayisi= " + String.valueOf(gecenAracSayisi);
    	}
    }
    public static class Car {
    	int numberOfStreet;
    	List<String> listStreet;
    	int totalSec = 0;
    	public Car(int size) {
    		listStreet = new ArrayList<String>(size);
    		numberOfStreet=0;
    		totalSec=0;
    	}
    	public void addStreet(String s, int sec) {
    		
    		this.listStreet.add(s);
    		if(numberOfStreet>0)
    			this.totalSec+=sec;
    		numberOfStreet++;
    	}
    	public String toString() {
    		String ss="";
    		for(String s:listStreet) {
    			ss+=s + " ";
    		}
    		return ss + " | totalSec = "+totalSec;
    	}
    	

    }
    static class Intersection {
    	List<Street> income;
    	List<Street> outgoing;
    	public Intersection() {
    		income = new ArrayList<Street>();
    		outgoing = new ArrayList<Street>();
    		
    	}
    }
    public static int ebob(int n1,int n2) {
    	 int ebob = 1;
    	 
         for(int i = 1; i <= n1 && i <= n2; ++i)
         {
             if(n1 % i==0 && n2 % i==0)
                 ebob = i;
         }
  
         return ebob;
   }


}
