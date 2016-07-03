package pmedit;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;

public class WindowsRegisterContextMenu {


	public static String pdfFileType(boolean create){
		String pdfFileType = null ;
		if (false){
		} else if(hasRegistryKey(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Classes\\.pdf")){
			pdfFileType = Advapi32Util.registryGetStringValue( WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Classes\\.pdf", "");
		} else if(hasRegistryKey(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Classes\\.pdf")){
			pdfFileType = Advapi32Util.registryGetStringValue( WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Classes\\.pdf", "");
		} else if(create){
			pdfFileType = "pdffiletype";
			Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Classes", ".pdf");
			Advapi32Util.registrySetStringValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Classes\\.pdf", "", pdfFileType);
		}
		return pdfFileType;
	}
	
	public static String applicationKey(){
		return "SOFTWARE\\Classes\\PdfMetadataEditor";
	}
	
	public static String editCmdShellKey(String pdfFileType){
		return "SOFTWARE\\Classes\\" + pdfFileType + "\\shell\\Pdf metadata edit";		
	}

	public static String batchMenuShellKey(String pdfFileType){
		return "SOFTWARE\\Classes\\" + pdfFileType + "\\shell\\PME.Batch.Menu";		
	}

	public static String batchCmdShellKey(String pdfFileType){
		return "SOFTWARE\\Classes\\" + pdfFileType + "\\Batch.Menu\\shell";		
	}
	
	public static void createRegistryKey(String keyToCreate){
		String[] keys = keyToCreate.split("\\\\");
		String current = "";
		for(String key: keys){
			current += key;
			if(!Advapi32Util.registryKeyExists(WinReg.HKEY_CURRENT_USER, current)){
				Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, current);
			}
			current += "\\";
		}
		
	}

	public static boolean hasRegistryKey(com.sun.jna.platform.win32.WinReg.HKEY root, String keyToCreate){
		String[] keys = keyToCreate.split("\\\\");
		String current = "";
		for(String key: keys){
			current += key;
			//System.out.printf("\nCheck key: %s : ", current);
			if(!Advapi32Util.registryKeyExists(root, current)){
				//System.out.printf("false\n");
				return false;
			}
			current += "\\";
		}
		current = current.substring(0,current.length()-1);
		//System.out.printf("\nCheck key: %s\n", Advapi32Util.registryValueExists(root, current, ""));
		return Advapi32Util.registryValueExists(root, current,"");
	}

	public static void register() throws Exception{
			String pdfFileType = pdfFileType(true);
			String thisJarDir;
			try {
				thisJarDir = new File(PreferencesWindow.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParentFile().getAbsolutePath();
				
			} catch (URISyntaxException e) {
				throw new Exception("Cannot find the path to current jar");
			}
			String exePath = "\"" + thisJarDir +File.separator + "PdfMetadataEditor.exe\"";
			String shellKey = editCmdShellKey(pdfFileType);
			String shellCommandKey = shellKey +"\\command";
			String shellDdeExecKey = shellKey +"\\ddeexec";
			String shellDdeExecApplicationKey = shellDdeExecKey +"\\application";

			createRegistryKey(shellCommandKey);
			createRegistryKey(shellDdeExecKey);
			createRegistryKey(shellDdeExecApplicationKey);
			Advapi32Util.registrySetStringValue(WinReg.HKEY_CURRENT_USER, shellKey, "", "Pdf metadata editor");
			Advapi32Util.registrySetStringValue(WinReg.HKEY_CURRENT_USER, shellCommandKey, "", exePath );
			Advapi32Util.registrySetStringValue(WinReg.HKEY_CURRENT_USER, shellDdeExecKey, "", "\"%1\"");
			Advapi32Util.registrySetStringValue(WinReg.HKEY_CURRENT_USER, shellDdeExecApplicationKey, "", "PdfMetadataEditor");
			
			// Add batch commands
			String batchMenuShellKey = batchMenuShellKey(pdfFileType);
			createRegistryKey(batchMenuShellKey);
			Advapi32Util.registrySetStringValue(WinReg.HKEY_CURRENT_USER, batchMenuShellKey, "MUIVerb", "Pdf metadata batch");
			Advapi32Util.registrySetStringValue(WinReg.HKEY_CURRENT_USER, batchMenuShellKey, "ExtendedSubCommandsKey", pdfFileType + "\\Batch.Menu");
			List<CommandDescription> reverseCmdList = Arrays.asList(CommandDescription.batchCommands);
			Collections.reverse(reverseCmdList);
			for(CommandDescription desc:  reverseCmdList){
				String batchShellKey = batchCmdShellKey(pdfFileType) + "\\" + desc.regKey;
				String batchShellCommandKey = batchShellKey + "\\command";
				String batchShellDdeExecKey = batchShellKey +"\\ddeexec";
				String batchShellDdeExecApplicationKey = shellDdeExecKey +"\\application";
				createRegistryKey(batchShellCommandKey);
				createRegistryKey(batchShellDdeExecKey);
				createRegistryKey(batchShellDdeExecApplicationKey);
				Advapi32Util.registrySetStringValue(WinReg.HKEY_CURRENT_USER, batchShellKey, "MUIVerb", desc.description);
				Advapi32Util.registrySetStringValue(WinReg.HKEY_CURRENT_USER, batchShellCommandKey, "", exePath + " " + desc.name);
				Advapi32Util.registrySetStringValue(WinReg.HKEY_CURRENT_USER, batchShellDdeExecKey, "", desc.name +" \"%1\"");
				Advapi32Util.registrySetStringValue(WinReg.HKEY_CURRENT_USER, batchShellDdeExecApplicationKey, "", "PdfMetadataEditor");
			}
	}
	
	public static void unregister() {
		String pdfFileType = pdfFileType(false);
		if(pdfFileType != null){
			String shellKey = editCmdShellKey(pdfFileType);
			String shellCommandKey = shellKey +"\\command";
			String shellDdeExecKey = shellKey +"\\ddeexec";
			String shellDdeExecApplicationKey = shellDdeExecKey +"\\application";

			Advapi32Util.registryDeleteKey(WinReg.HKEY_CURRENT_USER, shellDdeExecApplicationKey);			
			Advapi32Util.registryDeleteKey(WinReg.HKEY_CURRENT_USER, shellDdeExecKey);
			Advapi32Util.registryDeleteKey(WinReg.HKEY_CURRENT_USER, shellCommandKey);
			Advapi32Util.registryDeleteKey(WinReg.HKEY_CURRENT_USER, shellKey);

			// Batch commands
			for(CommandDescription desc:  CommandDescription.batchCommands){
				String batchShellKey = batchCmdShellKey(pdfFileType) + "\\" + desc.regKey;
				String batchShellCommandKey = batchShellKey + "\\command";
				String batchShellDdeExecKey = batchShellKey +"\\ddeexec";
				String batchShellDdeExecApplicationKey = shellDdeExecKey +"\\application";

				Advapi32Util.registryDeleteKey(WinReg.HKEY_CURRENT_USER, batchShellDdeExecApplicationKey);
				Advapi32Util.registryDeleteKey(WinReg.HKEY_CURRENT_USER, batchShellDdeExecKey);
				Advapi32Util.registryDeleteKey(WinReg.HKEY_CURRENT_USER, batchShellCommandKey);
				Advapi32Util.registryDeleteKey(WinReg.HKEY_CURRENT_USER, batchShellKey);
			}
			Advapi32Util.registryDeleteKey(WinReg.HKEY_CURRENT_USER, batchMenuShellKey(pdfFileType));
		}
	}
	
	public static void main(String[] args) {
		if(args.length == 0 ){
			System.out.println("Specify register or unregister as first argument");
			return;
		}
		if(args[0].toLowerCase().equals("register")){
			try {
				register();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(args[0].toLowerCase().equals("unregister")){
			try {
				unregister();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
