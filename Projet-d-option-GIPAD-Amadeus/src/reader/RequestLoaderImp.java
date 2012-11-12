package reader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

import context.userConstraints.cg.CG;
import context.userConstraints.cve.CVE;
import context.userConstraints.cvf.CVF;
import context.userConstraints.cvo.CVO;

public class RequestLoaderImp implements RequestLoader {

    public static final String DOUBLE_POINT = ":";
    
    public static final String COMMENTAIRE = "#";
    
    public static final String SEPARATEUR = ",";
    
    public static final String CVO="CVO";
    
    public static final String CVF="CVF";
    
    public static final String CVE="CVE";
    
    public static final String CG="CG";
    
    public static final int TAILLE_CV=3;
    
    public static final int TAILLE_CG=2;
    
    private CVO cvo;
    
    private CVF cvf;
    
    @Override
    public void loadRequest(final String dir) {
        Scanner sc;
        try {
            sc = new Scanner(new File(dir));
            String s="";
            while(sc.hasNextLine()){
                
                s=sc.nextLine();
                if(s.substring(0, TAILLE_CV).equals(CVO)){
                    String CVO0 = getStringPropre(s);
                    s=sc.nextLine();
                    String CVO10 = getStringPropre(s).split(SEPARATEUR)[0];
                    String CVO11 = getStringPropre(s).split(SEPARATEUR)[1];
                    s=sc.nextLine();
                    String CVO20 = getStringPropre(s).split(SEPARATEUR)[0];
                    String CVO21 = getStringPropre(s).split(SEPARATEUR)[1];
                    cvo= new CVO(CVO0, CVO10, CVO11, CVO20, CVO21);
                }
                
                if(s.substring(0, TAILLE_CV).equals(CVE)){
                    s=sc.nextLine();
                    String CVE0 = getStringPropre(s);
                    s=sc.nextLine();
                    String CVO10 = getStringPropre(s).split(SEPARATEUR)[0];
                    String CVO11 = getStringPropre(s).split(SEPARATEUR)[1];
                    s=sc.nextLine();
                    String CVO20 = getStringPropre(s).split(SEPARATEUR)[0];
                    String CVO21 = getStringPropre(s).split(SEPARATEUR)[1];
                    cvo= new CVO(CVO0, CVO10, CVO11, CVO20, CVO21);
                    
                }
                
                if (s.substring(0, TAILLE_CV).equals(CVF)){
                    String CVF0 = getStringPropre(s);
                    s=sc.nextLine();
                    String CVF10 = getStringPropre(s).split(SEPARATEUR)[0];
                    String CVF11 = getStringPropre(s).split(SEPARATEUR)[1];
                    s=sc.nextLine();
                    String CVF20 = getStringPropre(s).split(SEPARATEUR)[0];
                    String CVF21 = getStringPropre(s).split(SEPARATEUR)[1];
                    cvf= new CVF(CVF0, CVF10, CVF11, CVF20, CVF21);
                }
            }
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        

    }
    
    

    @Override
    public CVO getCVO() {
        return cvo;
    }

    @Override
    public CVF getCVF() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<CVE> getCVEs() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<CG> getCGs() {
        // TODO Auto-generated method stub
        return null;
    }

    private String getStringPropre(String s){
        return s.split(DOUBLE_POINT)[1].split(COMMENTAIRE)[0].trim();
    }

}
