/*
 * Created on 8/Mai/2005 - 11:53:40
 * 
 */

package net.sourceforge.fenixedu.dataTransferObject.inquiries;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sourceforge.fenixedu.dataTransferObject.InfoExecutionCourse;
import net.sourceforge.fenixedu.dataTransferObject.InfoObject;
import net.sourceforge.fenixedu.dataTransferObject.InfoTeacher;
import net.sourceforge.fenixedu.dataTransferObject.teacher.InfoNonAffiliatedTeacher;
import net.sourceforge.fenixedu.domain.CourseLoad;
import net.sourceforge.fenixedu.domain.DomainObject;
import net.sourceforge.fenixedu.domain.ShiftType;

/**
 * @author Jo�o Fialho & Rita Ferreira
 *
 */
public class InfoTeacherOrNonAffiliatedTeacherWithRemainingClassTypes extends
InfoObject {

    private InfoTeacher teacher;
    private InfoNonAffiliatedTeacher nonAffiliatedTeacher;
    private String teacherName;

    final private List<ShiftType> remainingClassTypes = new ArrayList<ShiftType>();
    private Boolean hasEvaluations = false;


    public InfoTeacherOrNonAffiliatedTeacherWithRemainingClassTypes(InfoObject infoTeacherOrNonAffiliatedTeacher, InfoExecutionCourse infoExecutionCourse) {

	if(infoTeacherOrNonAffiliatedTeacher instanceof InfoTeacher) {
	    this.teacher = (InfoTeacher) infoTeacherOrNonAffiliatedTeacher;
	    this.nonAffiliatedTeacher = null;
	    this.setIdInternal(this.teacher.getIdInternal());
	    this.teacherName = this.teacher.getInfoPerson().getNome();

	} else if(infoTeacherOrNonAffiliatedTeacher instanceof InfoNonAffiliatedTeacher) {

	    this.teacher = null;
	    this.nonAffiliatedTeacher = (InfoNonAffiliatedTeacher) infoTeacherOrNonAffiliatedTeacher;
	    this.setIdInternal(this.nonAffiliatedTeacher.getIdInternal());
	    this.teacherName = this.nonAffiliatedTeacher.getName();
	}
	
	Map<ShiftType, CourseLoad> courseLoadsMap = infoExecutionCourse.getExecutionCourse().getCourseLoadsMap();
	
	if(courseLoadsMap.containsKey(ShiftType.TEORICA) && !courseLoadsMap.get(ShiftType.TEORICA).isEmpty()) {
	    this.remainingClassTypes.add(ShiftType.TEORICA);
	}
	if(courseLoadsMap.containsKey(ShiftType.PRATICA) && !courseLoadsMap.get(ShiftType.PRATICA).isEmpty()) {
	    this.remainingClassTypes.add(ShiftType.PRATICA);
	}
	if(courseLoadsMap.containsKey(ShiftType.LABORATORIAL) && !courseLoadsMap.get(ShiftType.LABORATORIAL).isEmpty()) {
	    this.remainingClassTypes.add(ShiftType.LABORATORIAL);
	}
	if(courseLoadsMap.containsKey(ShiftType.TEORICO_PRATICA) && !courseLoadsMap.get(ShiftType.TEORICO_PRATICA).isEmpty()) {
	    this.remainingClassTypes.add(ShiftType.TEORICO_PRATICA);			
	}
	if(courseLoadsMap.containsKey(ShiftType.SEMINARY) && !courseLoadsMap.get(ShiftType.SEMINARY).isEmpty()) {
	    this.remainingClassTypes.add(ShiftType.SEMINARY);
	}
	if(courseLoadsMap.containsKey(ShiftType.PROBLEMS) && !courseLoadsMap.get(ShiftType.PROBLEMS).isEmpty()) {
	    this.remainingClassTypes.add(ShiftType.PROBLEMS);
	}
	if(courseLoadsMap.containsKey(ShiftType.FIELD_WORK) && !courseLoadsMap.get(ShiftType.FIELD_WORK).isEmpty()) {
	    this.remainingClassTypes.add(ShiftType.FIELD_WORK);
	}
	if(courseLoadsMap.containsKey(ShiftType.TRAINING_PERIOD) && !courseLoadsMap.get(ShiftType.TRAINING_PERIOD).isEmpty()) {
	    this.remainingClassTypes.add(ShiftType.TRAINING_PERIOD);
	}
	if(courseLoadsMap.containsKey(ShiftType.TUTORIAL_ORIENTATION) && !courseLoadsMap.get(ShiftType.TUTORIAL_ORIENTATION).isEmpty()) {
	    this.remainingClassTypes.add(ShiftType.TUTORIAL_ORIENTATION);
	}
    }

    public InfoTeacherOrNonAffiliatedTeacherWithRemainingClassTypes () {

    }


    /**
     * @return Returns the remainingClassTypes.
     */
    public List<ShiftType> getRemainingClassTypes() {
	return remainingClassTypes;
    }

    /**
     * @return Returns the nonAffiliatedTeacher.
     */
    public InfoNonAffiliatedTeacher getNonAffiliatedTeacher() {
	return nonAffiliatedTeacher;
    }


    /**
     * @param nonAffiliatedTeacher The nonAffiliatedTeacher to set.
     */
    public void setNonAffiliatedTeacher(
	    InfoNonAffiliatedTeacher nonAffiliatedTeacher) {
	this.nonAffiliatedTeacher = nonAffiliatedTeacher;
    }


    /**
     * @return Returns the teacher.
     */
    public InfoTeacher getTeacher() {
	return teacher;
    }


    /**
     * @param teacher The teacher to set.
     */
    public void setTeacher(InfoTeacher teacher) {
	this.teacher = teacher;
    }


    /**
     * @return Returns the teacherName.
     */
    public String getTeacherName() {
	return teacherName;
    }


    /**
     * @param teacherName The teacherName to set.
     */
    public void setTeacherName(String teacherName) {
	this.teacherName = teacherName;
    }



    /**
     * @return Returns the hasEvaluations.
     */
    public Boolean getHasEvaluations() {
	return hasEvaluations;
    }


    /**
     * @param hasEvaluations The hasEvaluations to set.
     */
    public void setHasEvaluations(Boolean hasEvaluations) {
	this.hasEvaluations = hasEvaluations;
    }


    public String toString() {
	String result = "[INFOTEACHERORNONAFFILIATEDTEACHERWITHREMAININGCLASSTYPES";

	if(this.teacher != null) {
	    result += ", " + this.teacher.toString();

	} else if(this.nonAffiliatedTeacher != null) {
	    result += ", " + this.nonAffiliatedTeacher.toString();
	}

	result += this.remainingClassTypes.toString() + "]\n";
	return result;
    }


    /* (non-Javadoc)
     * @see net.sourceforge.fenixedu.dataTransferObject.InfoObject#copyFromDomain(net.sourceforge.fenixedu.domain.DomainObject)
     */
    public void copyFromDomain(DomainObject domainObject) {
	// TODO Auto-generated method stub
	super.copyFromDomain(domainObject);
    }



}
