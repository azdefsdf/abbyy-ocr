package config;



public class SamplesConfig {
	// Folder with FCEngine.dll
	public static String GetDllPath() {
		if( is64BitJVMArchitecture() ) {
			return "C:\\Program Files\\ABBYY SDK\\12\\FlexiCapture SDK\\Bin64";
		} else {
			return "Directory\\where\\x86\\dll\\resides";
		}
	}

	// Return customer project id for FC SDK
	public static String GetCustomerProjectId() {
		return "Q3cugU47yTFt47mYx8Jw";
	}

	// Return path to license file
	public static String GetLicensePath() {
		return "";
	}

	// Return license password
	public static String GetLicensePassword() {
		return "";
	}

	// Return full path to Samples directory
	public static String GetSamplesFolder() {
		return "C:\\ProgramData\\ABBYY\\FCSDK\\12\\FlexiCapture SDK\\Samples";
	}

	// Determines whether the JVM architecture is a 64-bit architecture
	private static boolean is64BitJVMArchitecture()
	{
		String jvmKeys [] = {
			"sun.arch.data.model", 
			"com.ibm.vm.bitmode", 
			"os.arch"
		};
		for( String key : jvmKeys ) {
			String property = System.getProperty( key );
			if( property != null ) {
				if( property.indexOf( "64" ) >= 0 ) {
					return true;
				} else if( property.indexOf( "32" ) >= 0 ) {
					return false;
				} else if( property.indexOf( "86" ) >= 0 ) {
					return false;
				}
			}
		}
		return false;
	}
}
