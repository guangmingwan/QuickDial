package xu.ye.uitl;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.content.Intent;

/**
 * @author LDM
 * IntentUtil.java
 * 2011-12-11 ����11:59:40
 */

public class BaseIntentUtil {
	
	/**
	 * IntentUtilĬ�ϵ���ת����
	 * ʹ��ǰ��ҪԤ������
	 */
	public static int DEFAULT_ENTER_ANIM;
	public static int DEFAULT_EXIT_ANIM;
	
	private static Intent intent;
	
	/**
	 * �Զ��嶯�� ʹ��
	 * DEFAULT_ENTER_ANIM �� DEFAULT_EXIT_ANIM ��Ϊ����Ч��
	 * @param activity
	 * @param classes
	 */
	public static void intentDIY(Activity activity, Class<?> classes){
		intentDIY(activity, classes, null, DEFAULT_ENTER_ANIM, DEFAULT_EXIT_ANIM);
	}
	
	/**
	 * �Զ��嶯�� ʹ��
	 * DEFAULT_ENTER_ANIM �� DEFAULT_EXIT_ANIM ��Ϊ����Ч��
	 * @param activity
	 * @param classes
	 * @param paramMap �������
	 */
	public static void intentDIY(Activity activity, Class<?> classes, Map<String, String> paramMap){
		intentDIY(activity, classes, paramMap, DEFAULT_ENTER_ANIM, DEFAULT_EXIT_ANIM);
	}
	
	/**
	 * �Զ��嶯��
	 * @param activity
	 * @param classes
	 * @param enterAnim enter��ԴID
	 * @param exitAnim  exit��ԴID
	 */
	public static void intentDIY(Activity activity, Class<?> classes, int enterAnim, int exitAnim){
		intentDIY(activity, classes, null, enterAnim ,exitAnim);
	}
	
	/**
	 * �Զ��嶯��
	 * @param activity
	 * @param classes
	 * @param paramMap  �������
	 * @param enterAnim enter��ԴID
	 * @param exitAnim  exit��ԴID
	 */
	public static void intentDIY(Activity activity, Class<?> classes, Map<String, String> paramMap, int enterAnim, int exitAnim){
		intent = new Intent(activity, classes);
		organizeAndStart(activity, classes, paramMap);
		if(enterAnim != 0 && exitAnim != 0){
			activity.overridePendingTransition(enterAnim, exitAnim);
		}
	}
	
	/**
	 * ϵͳĬ��
	 * @param activity
	 * @param classes
	 * @param paramMap
	 */
	public static void intentSysDefault(Activity activity, Class<?> classes, Map<String, String> paramMap){
		organizeAndStart(activity, classes, paramMap);
	}
	
	private static void organizeAndStart(Activity activity, Class<?> classes, Map<String, String> paramMap){
		intent = new Intent(activity, classes);
		if(null != paramMap){
			Set<String> set = paramMap.keySet();
			for (Iterator<String> iterator = set.iterator(); iterator.hasNext();) {
				String key = iterator.next();
				intent.putExtra(key, paramMap.get(key));
			}
		}
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		activity.startActivity(intent);
	}

}
