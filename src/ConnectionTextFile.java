import java.io.*;
import java.lang.*;
import java.util.*;


public class ConnectionTextFile 
{
	//----------------------
	//變數宣告區
	char[][] dbData;           	// 存放「交易資料庫的每個項目」
	int[] transProfit;			// 記錄「每筆交易的 profit 值」
	double[] threshold; //紀錄每個item threshold
	double totalGain=0;      	// 記錄「全部的 Profit 值」
	double[] maxGainThreshold; //紀錄紀錄每個item gain_threshold

	String FileName;           
	String FileName2;
	int num=0, num2=0;         					// 資料筆數

    public ConnectionTextFile(String FileName,String FileName2) 
    {
        try
        {
            this.FileName = FileName;
            this.FileName2 = FileName2;
  			//-----------------------------
  			// 01.
  			// 取得「資料庫交易記錄數量」
  			Get_DBSize();
  			Get_ThresholdSize();
  			//System.out.println(num);
    
  			//-----------------------------
  			// 02.
  			// 宣告「相關陣列大小」
         	dbData = new char[num][];
       		transProfit = new int[num];
  			threshold = new double[num2];
  			maxGainThreshold = new double[num2];
  			//-----------------------------
  			// 03.
  			// 讀取「交易資料庫」
  			buildDatabase();
 			//Arrays.fill(threshold,1); //所有item門檻值預設為1
 			//Arrays.fill(maxGainThreshold,totalGain); 
  			buildThreshold();
  				
            } 
            catch(Exception e) 
            {
                 System.out.println(e.getMessage());
            }
        }
        //------------------------------------------------------------
        //------------------------------------------------------------
        //------------------------------------------------------------
        //回傳資訊
        public char[][] getdbData() // 回傳「資料庫每筆交易記錄裡的item」
        {			
          return dbData;
        }

        public int[] getTransProfit() // 回傳「 每筆交易記錄的 transaction profit」
        {			
            return transProfit;
        }

        public double getTotalGain() // 回傳「 Total Gain 值」
        {  			  			
            return totalGain;
        }
        
        public double[] getThreshold() // 回傳「 Total Gain 值」
        {  			  			
            return threshold;
        }
        
        public double[] getGainThreshold() // 回傳「 Total Gain 值」
        {  			  			
            return maxGainThreshold;
        }  
        //------------------------------------------------------------
        //------------------------------------------------------------
        //------------------------------------------------------------
        
        String line;
        BufferedReader br;
        FileInputStream fis;//useless
        //----------------------------
        // 01.
        // 取得「資料庫交易記錄數量」
        public void Get_DBSize() //計算交易筆數
        {
            try
            {	
         	//------------------------------
  			//先獲得「資料庫筆數」
         	br = new BufferedReader(new FileReader(FileName+".txt"));
            while ((line=br.readLine())!=null)
            {
                this.num++; //統計資料筆數
            }   	
             br.close();
        }
        catch(Exception e)
        {
  			System.out.println(" Error about getting the size of database : "+e.toString());
  		}	
  		
        
    }
        
        public void Get_ThresholdSize() //計算item threshold個數
        {
            try
            {	
         	//------------------------------
  			//先獲得「資料庫筆數」
         	br = new BufferedReader(new FileReader(FileName2+".txt"));
            while ((line=br.readLine())!=null)
            {
                this.num2++; //統計資料筆數
            }   	
             br.close();
        }
        catch(Exception e)
        {
  			System.out.println(" Error about getting the size of threshold : "+e.toString());
  		}	
  		
        
    }
    //----------------------------
    // 02.
    // 讀取「交易資料庫」	
    public void buildDatabase()
    {
        try
        {
            this.num=0;
            
            br = new BufferedReader(new FileReader(FileName+".txt"));
            while ((line=br.readLine())!=null)//統計資料筆數
            {    
                // 01.
         		// 記錄「此筆交易記錄的 items 和 profit」
         		String[] trans = line.split("_");  
        		String[] data = trans[1].split(", ");			
        		dbData[num] = new char[data.length];					
                transProfit[num] = Integer.parseInt(trans[0]);		//紀錄每筆交易的利潤
                totalGain +=transProfit[num];						//算總利潤
                int pos=0;
                for(int i=0;i<data.length;i++)
                {   // 02. 
         			// 記錄「item」
         			int item = Integer.parseInt(data[i]);         	//第num筆交易第pos個item			
                    dbData[num][pos] = (char)item;
                    //System.out.print((int)dbData[num][pos]);
     				pos++;
         		}
                
                 this.num++;         			
            }	
                 br.close();
        }
        catch(Exception e)
        {
            System.out.println(" Error about loading the dataset into the memory : "+e.toString());
        }
    }
    
    public void buildThreshold()
    {
        try
        {
            
            br = new BufferedReader(new FileReader(FileName2+".txt"));
            while ((line=br.readLine())!=null)//統計資料筆數
            {    
         		// 記錄每個item的threshold
         		String[] thresh = line.split("_");  							
                threshold[Integer.parseInt(thresh[0])] = Double.parseDouble(thresh[1]);
                maxGainThreshold[Integer.parseInt(thresh[0])] = Double.parseDouble(thresh[1])*totalGain;
            }	
                 br.close();
        }
        catch(Exception e)
        {
            System.out.println(" Error about loading the ThresholdSet into the memory : "+e.toString());
        }
    }
}