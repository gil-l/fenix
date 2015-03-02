/**
 * Copyright © 2002 Instituto Superior Técnico
 *
 * This file is part of FenixEdu Academic.
 *
 * FenixEdu Academic is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu Academic is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu Academic.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.academic.report.academicAdministrativeOffice;

import java.math.BigDecimal;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.fenixedu.academic.domain.Country;
import org.fenixedu.academic.domain.DegreeOfficialPublication;
import org.fenixedu.academic.domain.DegreeSpecializationArea;
import org.fenixedu.academic.domain.IEnrolment;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.degree.DegreeType;
import org.fenixedu.academic.domain.degreeStructure.CycleType;
import org.fenixedu.academic.domain.degreeStructure.EctsComparabilityPercentages;
import org.fenixedu.academic.domain.degreeStructure.EctsComparabilityTable;
import org.fenixedu.academic.domain.degreeStructure.EctsGraduationGradeConversionTable;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.academic.domain.organizationalStructure.AcademicalInstitutionType;
import org.fenixedu.academic.domain.organizationalStructure.Unit;
import org.fenixedu.academic.domain.organizationalStructure.UniversityUnit;
import org.fenixedu.academic.domain.phd.serviceRequests.documentRequests.PhdDiplomaSupplementRequest;
import org.fenixedu.academic.domain.serviceRequests.IDiplomaSupplementRequest;
import org.fenixedu.academic.domain.serviceRequests.documentRequests.DiplomaSupplementRequest;
import org.fenixedu.academic.domain.serviceRequests.documentRequests.IDocumentRequest;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.academic.domain.student.curriculum.ExtraCurricularActivity;
import org.fenixedu.academic.domain.student.curriculum.ExtraCurricularActivityType;
import org.fenixedu.academic.domain.student.curriculum.ICurriculumEntry;
import org.fenixedu.academic.domain.studentCurriculum.Dismissal;
import org.fenixedu.academic.domain.studentCurriculum.ExternalEnrolment;
import org.fenixedu.academic.util.Bundle;
import org.fenixedu.academic.util.StringFormatter;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.commons.i18n.LocalizedString;
import org.joda.time.DateTime;

public class DiplomaSupplement extends AdministrativeOfficeDocument {

    protected DiplomaSupplement(final IDocumentRequest documentRequest, final Locale locale) {
        super(documentRequest, locale);
    }

    @Override
    public String getReportTemplateKey() {
        return getClass().getName();
    }

    @Override
    protected IDiplomaSupplementRequest getDocumentRequest() {
        return (IDiplomaSupplementRequest) documentRequestDomainReference;
    }

    @Override
    protected void fillReport() {

        //FIXME Fix all the inelegant localizedString constructions in here
        //      Building LocalizedString getters throughout the domain instead of locale receiving string getters would help
        final IDiplomaSupplementRequest documentRequest = getDocumentRequest();

        addParameter("name", StringFormatter.prettyPrint(documentRequest.getPerson().getName().trim()));

        // Group 1
        final Person person = documentRequest.getPerson();
        addParameter("familyName", person.getFamilyNames());
        addParameter("givenName", person.getGivenNames());
        addParameter("birthDay", getBirthDate(person));
        addParameter("nationality", getNationality(person.getCountry()));
        addParameter("idDocumentType", BundleUtil.getLocalizedString(Bundle.ENUMERATION, person.getIdDocumentType().getName()));
        addParameter("idDocumentNumber", person.getDocumentIdNumber());
        addParameter("registrationNumber", documentRequest.getRegistrationNumber());

        // Group 2
        DegreeOfficialPublication degreePublication = documentRequest.getDegreeOfficialPublication();
        if (degreePublication == null) {
            throw new DomainException("error.DiplomaSupplement.degreeOfficialPublicationNotFound");
        }
        DegreeType degreeType = degreePublication.getDegree().getDegreeType();
        LocalizedString graduateTitle =
                BundleUtil.getLocalizedString(Bundle.ENUMERATION, degreeType.getQualifiedName() + ".graduate.title");

        LocalizedString.Builder degreeDesignation = new LocalizedString.Builder();
        String title;
        for (Locale l : CoreConfiguration.supportedLocales()) {
            title = documentRequest.getGraduateTitle(getLocale());
            title = title.replace("Licenciado", "Licenciatura");
            title = title.replace("Graduate", "Graduation");
            title = title.replace("Mestre", "Mestrado");
            title = title.replace("Doutor", "Doutoramento");
            title = title.replace("Doctor", "Doctoral Programme");
            degreeDesignation = degreeDesignation.with(l, title);
        }

        LocalizedString.Builder prevailingScientificArea = new LocalizedString.Builder();
        for (Locale l : CoreConfiguration.supportedLocales()) {
            prevailingScientificArea = prevailingScientificArea.with(l, documentRequest.getPrevailingScientificArea(l));
        }

        final UniversityUnit university = getUniversity(documentRequest.getRequestDate());
        LocalizedString universityStatus =
                BundleUtil.getLocalizedString(Bundle.ENUMERATION, AcademicalInstitutionType.class.getSimpleName() + "."
                        + university.getInstitutionType().getName());

        final Unit institutionUnit = Bennu.getInstance().getInstitutionUnit();
        LocalizedString institutionStatus =
                BundleUtil.getLocalizedString(Bundle.ENUMERATION, Bennu.getInstance().getInstitutionUnit().getType().getName());
        institutionStatus =
                institutionStatus.append(BundleUtil.getLocalizedString(Bundle.ACADEMIC, "label.of.female"), SINGLE_SPACE);
        institutionStatus = institutionStatus.append(university.getNameI18n().toLocalizedString(), SINGLE_SPACE);

        LocalizedString languages = BundleUtil.getLocalizedString(Bundle.ENUMERATION, "pt");
        if (!documentRequest.getRequestedCycle().equals(CycleType.FIRST_CYCLE)) {
            languages =
                    languages.append(BundleUtil.getLocalizedString(Bundle.ACADEMIC, "label.and"), SINGLE_SPACE).append(
                            BundleUtil.getLocalizedString(Bundle.ENUMERATION, "en"), SINGLE_SPACE);
        }

        addParameter("graduateTitle", graduateTitle);
        addParameter("degreeDesignation", degreeDesignation.build());
        addParameter("prevailingScientificArea", prevailingScientificArea.build());
        addParameter("universityName", university.getNameI18n().toLocalizedString());
        addParameter("universityStatus", universityStatus);
        addParameter("institutionName", institutionUnit.getNameI18n().toLocalizedString());
        addParameter("institutionStatus", institutionStatus);
        addParameter("languages", languages);

        // Group 3
        final double ectsCreditsValue = documentRequest.getEctsCredits();
        final String ectsCredits =
                (long) ectsCreditsValue == ectsCreditsValue ? "" + Long.toString((long) ectsCreditsValue) : Double
                        .toString(ectsCreditsValue);
        final LocalizedString qualificationLevel =
                BundleUtil.getLocalizedString(Bundle.ACADEMIC,
                        "diploma.supplement.qualification." + documentRequest.getRequestedCycle());

        addParameter("qualificationLevel", qualificationLevel);
        addParameter("years", documentRequest.getNumberOfCurricularYears());
        addParameter("semesters", documentRequest.getNumberOfCurricularSemesters());
        addParameter("weeksOfStudyPerYear", 40); //FIXME inelegant, find a getter or put it in template. It was in the resources before...
        addParameter("ectsCredits", ectsCredits);

        // Group 4
        final String fAQKey = documentRequest.getFinalAverageQualified();

        EctsGraduationGradeConversionTable table = documentRequest.getGraduationConversionTable();
        List<Double> percentage = new ArrayList<Double>();
        List<String> ects = new ArrayList<String>();
        Map<String, List<?>> classificationSystemTable = new HashMap<String, List<?>>();
        classificationSystemTable.put("percentage", percentage);
        classificationSystemTable.put("ects", ects);
        EctsComparabilityPercentages p = table.getPercentages();
        EctsComparabilityTable e = table.getEctsTable();
        for (int i = 10; i <= 20; i++) {
            percentage.add(p.getPercentage(i));
            ects.add(e.convert(i));
        }

        LocalizedString.Builder thesisFinalGrade = new LocalizedString.Builder();
        if (documentRequest.isRequestForPhd()) {
            PhdDiplomaSupplementRequest phdDocumentRequest = ((PhdDiplomaSupplementRequest) documentRequest);
            for (Locale l : CoreConfiguration.supportedLocales()) {
                thesisFinalGrade = thesisFinalGrade.with(l, phdDocumentRequest.getThesisFinalGrade(getLocale()));
            }
        }

        String officialPublication = "";
        LocalizedString.Builder specAreas = new LocalizedString.Builder();
        int nrSpecAreas = 0;
        if (documentRequest.isRequestForPhd()) {
            officialPublication = degreePublication.getOfficialReference();
        } else if (!documentRequest.getRequestedCycle().equals(CycleType.FIRST_CYCLE)
                && degreePublication.getSpecializationAreaSet().size() != 0) {
            nrSpecAreas = degreePublication.getSpecializationAreaSet().size();
            boolean first = true;
            for (DegreeSpecializationArea area : degreePublication.getSpecializationAreaSet()) {
                if (first) {
                    specAreas = specAreas.append(area.getName().toLocalizedString());
                    first = false;
                } else {
                    specAreas = specAreas.append(area.getName().toLocalizedString(), "; ");
                }
            }
            officialPublication = degreePublication.getOfficialReference();
        }

        final List<AcademicUnitEntry> identifiers = new ArrayList<AcademicUnitEntry>();
        final List<DiplomaSupplementEntry> entries = new ArrayList<DiplomaSupplementEntry>();

        if (documentRequest.hasRegistration()) {
            Registration registration = documentRequest.getRegistration();
            final Map<Unit, String> academicUnitIdentifiers = new HashMap<Unit, String>();
            for (ICurriculumEntry entry : registration.getCurriculum(documentRequest.getRequestedCycle()).getCurriculumEntries()) {
                entries.add(new DiplomaSupplementEntry(entry, academicUnitIdentifiers));
            }
            Collections.sort(entries);

            for (final Entry<Unit, String> entry : academicUnitIdentifiers.entrySet()) {
                identifiers.add(new AcademicUnitEntry(entry.getKey(), entry.getValue()));
            }
            Collections.reverse(identifiers);
        }

        addParameter("specAreas", specAreas.build());
        addParameter("nrSpecAreas", nrSpecAreas);
        addParameter("ectsCredits", ectsCredits);
        addParameter("officialPublication", officialPublication);
        addParameter("finalAverage", documentRequest.getFinalAverage());
        addParameter("finalAverageQualified", (fAQKey != null) ? BundleUtil.getLocalizedString(Bundle.ACADEMIC, fAQKey) : "");
        addParameter("finalAverageECTS", table.getEctsTable().convert(documentRequest.getFinalAverage()));
        addParameter("classificationSystemTable", classificationSystemTable);
        addParameter("thesisFinalGrade", thesisFinalGrade.build());
        addParameter("entries", entries);
        addParameter("academicUnitIdentifiers", identifiers);

        // Group 5
        LocalizedString professionalOrder = new LocalizedString();
        LocalizedString professionalTitle = new LocalizedString();
        boolean profStatusApplicable = false;

        if (documentRequest.getRequestedCycle().equals(CycleType.SECOND_CYCLE)) {
            DiplomaSupplementRequest diplSuplRequest = (DiplomaSupplementRequest) documentRequest;
            String degreeSigla = diplSuplRequest.getRegistration().getDegree().getSigla();
            profStatusApplicable =
                    !degreeSigla.equals("MMA") && !degreeSigla.equals("MQ") && !degreeSigla.equals("MUOT")
                            && !degreeSigla.equals("MPOT") && !degreeSigla.equals("MFarm");
            if (profStatusApplicable) {
                String type =
                        "diploma.supplement.professionalstatus.credited." + (degreeSigla.equals("MA") ? "arquitect" : "engineer");
                professionalOrder = BundleUtil.getLocalizedString(Bundle.ACADEMIC, type + ".order");
                professionalTitle = BundleUtil.getLocalizedString(Bundle.ACADEMIC, type);
            }
        }

        addParameter("isAccessApplicable", !documentRequest.getRequestedCycle().equals(CycleType.THIRD_CYCLE));
        addParameter("isProfStatusApplicable", profStatusApplicable);
        addParameter("professionalOrder", professionalOrder);
        addParameter("professionalTitle", professionalTitle);

        // Group 6
        Student student = getDocumentRequest().getStudent();
        LocalizedString was = BundleUtil.getLocalizedString(Bundle.ACADEMIC, "diploma.supplement.was");
        LocalizedString between = BundleUtil.getLocalizedString(Bundle.ACADEMIC, "label.between");
        LocalizedString and = BundleUtil.getLocalizedString(Bundle.ACADEMIC, "label.and");
        List<LocalizedString> activities = new ArrayList<LocalizedString>();
        Map<String, List<LocalizedString>> activitiesTable = new HashMap<String, List<LocalizedString>>();
        if (!student.getExtraCurricularActivitySet().isEmpty()) {
            Map<ExtraCurricularActivityType, List<ExtraCurricularActivity>> activityMap =
                    new HashMap<ExtraCurricularActivityType, List<ExtraCurricularActivity>>();
            for (ExtraCurricularActivity activity : student.getExtraCurricularActivitySet()) {
                if (!activityMap.containsKey(activity.getType())) {
                    activityMap.put(activity.getType(), new ArrayList<ExtraCurricularActivity>());
                }
                activityMap.get(activity.getType()).add(activity);
            }
            for (Entry<ExtraCurricularActivityType, List<ExtraCurricularActivity>> entry : activityMap.entrySet()) {
                LocalizedString activityText = was;
                activityText = activityText.append(entry.getKey().getName().toLocalizedString(), SINGLE_SPACE);
                Iterator<ExtraCurricularActivity> it = entry.getValue().iterator();
                ExtraCurricularActivity activity;
                while (it.hasNext()) {
                    activity = it.next();
                    activityText = activityText.append(between, SINGLE_SPACE);
                    activityText = activityText.append(activity.getStart().toString("MM-yyyy"), SINGLE_SPACE);
                    activityText = activityText.append(and, SINGLE_SPACE);
                    activityText = activityText.append(activity.getEnd().toString("MM-yyyy"), SINGLE_SPACE);
                    if (it.hasNext()) {
                        activityText.append(", ");
                    }
                }
                activities.add(activityText);
            }
        }
        activitiesTable.put("activity", activities);
        addParameter("extraCurricularActivities", activitiesTable);

        // Group 7
        addParameter("universityPrincipal", UniversityUnit.getInstitutionsUniversityUnit().getCurrentPrincipal().getName());

        addParameter("isExemptedFromStudy", documentRequest.isExemptedFromStudy());
        addParameter("isForPhd", documentRequest.isRequestForPhd());
        addParameter("isForRegistration", documentRequest.isRequestForRegistration());

    }

    private LocalizedString getBirthDate(Person person) {
        LocalizedString.Builder bd = new LocalizedString.Builder();
        for (Locale l : CoreConfiguration.supportedLocales()) {
            bd = bd.with(l, person.getDateOfBirthYearMonthDay().toString(DD_SLASH_MM_SLASH_YYYY, l));
        }
        return bd.build();
    }

    private LocalizedString getNationality(Country country) {
        LocalizedString n = country.getCountryNationality().toLocalizedString();
        LocalizedString.Builder pn = new LocalizedString.Builder();
        for (Locale l : n.getLocales()) {
            pn.with(l, StringFormatter.prettyPrint(n.getContent(l)));
        }
        return pn.build();
    }

    static final public Comparator<DiplomaSupplementEntry> COMPARATOR = new Comparator<DiplomaSupplementEntry>() {

        @Override
        public int compare(DiplomaSupplementEntry o1, DiplomaSupplementEntry o2) {
            final int c = o1.getExecutionYear().compareTo(o2.getExecutionYear());
            return c == 0 ? Collator.getInstance().compare(o1.getName().getContent(), o2.getName().getContent()) : c;
        }

    };

    public class DiplomaSupplementEntry implements Comparable<DiplomaSupplementEntry> {

        private final ICurriculumEntry entry;

        private final String executionYear;

        private final LocalizedString name;

        private final LocalizedString type;

        private final LocalizedString duration;

        private final BigDecimal ectsCreditsForCurriculum;

        private final String gradeValue;

        private final String ectsScale;

        private final String academicUnitId;

        public DiplomaSupplementEntry(final ICurriculumEntry entry, final Map<Unit, String> academicUnitIdentifiers) {
            this.entry = entry;
            this.executionYear = entry.getExecutionYear().getYear();
            this.name = entry.getPresentationName().toLocalizedString();
            DateTime processingDate = computeProcessingDateToLockECTSTableUse();
            if (entry instanceof IEnrolment) {
                IEnrolment enrolment = (IEnrolment) entry;
                this.type = BundleUtil.getLocalizedString(Bundle.ENUMERATION, enrolment.getEnrolmentTypeName());
                this.duration =
                        BundleUtil
                                .getLocalizedString(
                                        Bundle.ACADEMIC,
                                        enrolment.isAnual() ? "diploma.supplement.duration.annual" : "diploma.supplement.duration.semestral");

                this.ectsScale =
                        enrolment.getEctsGrade(getDocumentRequest().getRegistration().getLastStudentCurricularPlan(),
                                processingDate).getValue();
            } else if (entry instanceof Dismissal && ((Dismissal) entry).getCredits().isEquivalence()) {
                Dismissal dismissal = (Dismissal) entry;
                this.type = BundleUtil.getLocalizedString(Bundle.ENUMERATION, dismissal.getEnrolmentTypeName());
                this.duration =
                        BundleUtil
                                .getLocalizedString(
                                        Bundle.ACADEMIC,
                                        dismissal.isAnual() ? "diploma.supplement.duration.annual" : "diploma.supplement.duration.semestral");
                this.ectsScale = dismissal.getEctsGrade(processingDate).getValue();
            } else {
                throw new Error("The roof is on fire");
            }
            this.ectsCreditsForCurriculum = entry.getEctsCreditsForCurriculum();
            this.academicUnitId = obtainAcademicUnitIdentifier(academicUnitIdentifiers);
            this.gradeValue = entry.getGrade().getValue();
        }

        private DateTime computeProcessingDateToLockECTSTableUse() {
            DateTime date = documentRequestDomainReference.getProcessingDate();
            return date != null ? date : new DateTime();
        }

        public ICurriculumEntry getEntry() {
            return entry;
        }

        public String getExecutionYear() {
            return executionYear;
        }

        public LocalizedString getName() {
            return name;
        }

        public LocalizedString getType() {
            return type;
        }

        public LocalizedString getDuration() {
            return duration;
        }

        public BigDecimal getEctsCreditsForCurriculum() {
            return ectsCreditsForCurriculum;
        }

        public String getGradeValue() {
            return gradeValue;
        }

        public String getEctsScale() {
            return ectsScale;
        }

        public String getAcademicUnitId() {
            return academicUnitId;
        }

        private String obtainAcademicUnitIdentifier(final Map<Unit, String> academicUnitIdentifiers) {
            final Unit unit =
                    entry instanceof ExternalEnrolment ? ((ExternalEnrolment) entry).getAcademicUnit() : Bennu.getInstance()
                            .getInstitutionUnit();
            return getAcademicUnitIdentifier(academicUnitIdentifiers, unit);
        }

        @Override
        public int compareTo(final DiplomaSupplementEntry o) {
            return COMPARATOR.compare(this, o);
        }

    }

    public class AcademicUnitEntry {
        private final String identifier;

        private final LocalizedString name;

        public AcademicUnitEntry(Unit unit, String identifier) {
            this.identifier = identifier;
            LocalizedString.Builder builder = new LocalizedString.Builder();

            builder.with(Locale.forLanguageTag("en-GB"), "Curricular Unit from ").with(Locale.forLanguageTag("pt-PT"),
                    "Unidade curricular da ");

            Unit univ = unit.getParentUnits().stream().filter(u -> u.isUniversityUnit()).findAny().orElse(null);
            if (univ != null) {
                builder.append(univ.getNameI18n().toLocalizedString());
                builder.append(", ");
            }
            builder.append(unit.getNameI18n().toLocalizedString());
            this.name = builder.build();
        }

        public String getIdentifier() {
            return identifier;
        }

        public LocalizedString getName() {
            return name;
        }
    }

}
