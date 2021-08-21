package com.spritehealth.sdk.model

import java.util.*
import kotlin.collections.ArrayList


class AccessTokenResponse{

    var token_type: String? = null

    var access_token: String? = null

    var refresh_token: String? = null

    var expires_in: Long? = null

    var scope: String? = null

    //Custom attributes

    var isSSO: Boolean? = null

    //Service Mesh purpose attributes
    var isValid: Boolean? = null

    var userIdentity: String? = null
}


class User {
    var id: Long? = null
    var name: String? = null
    var nickname: String? = null
    var email: String? = null
    var isCorporateAdmin = false
    var isVendorAdmin = false
    var isVendor = false
    var assignmentStatus: String? = null
    var mobilePhone: String? = null
    var textPhone: String? = null
    var landlinePhone: String? = null
    var imageIds: Set<Long>? = null
    var timeZone: String? = null

    //Redundant due to lastModifiedDate
    var lastAccessedDate: String? = null
    var authType = "NATIVE"

    /*TODO: need spelling correction */
    var prefferedLanguage: String? = null
    var status: String? = null

    /*===========
    =============Specialist Role fields
    ============================*/
    var accessLevel: String? = null
    var specialistLocationIds: Set<Long>? = null
    var licenseToPracticeStates: Set<String>? = null
    var consultingFee: Float? = null
    var memberFee: Float? = null
    var serviceFee: Float? = null
    var avgHCCRiskScore: Float? = null
    var vendorId: Long = 0
    var tin: String? = null
    var npi: String? = null
    var vendorNPI: String? = null
    var videoBioId: Long? = null
    var specialization: List<Int>? = null
    var availableServiceDefinitionIds: List<Long>? = null
    var vendorName: String? = null
    var acceptingNewPatients: Boolean? = null

    // Not persistent fields: For IMPORT/EXPORT Purposes --Starts here
    var specializationNames: String? = null
    var affiliations: List<String>? = null
    var tier: Int? = null
    var tierScore: Double? = null
    var accountableProviderId: Long? = null
    var gender: String? = null
    var maritalStatus: String? = null
    var dateOfBirth: String? = null
    var walletId: Long = 0
    var familyId: String? = null

    var employeeId: String? = null
    var designation: String? = null
    var insuranceAccount: String? = null
    var insurance: String? = null
    var groupNumber: String? = null
    var membershipId: String? = null
    var familyRelation: String? = null
    var referralSource: String? = null
    var contactPreference: String? = null
    var recommendedPlanId: Long = 0
    var recommendedPlanName: String? = null
    var healthAccountAccess = true //default true
    var lastCreditAmount = 0.0
    var paymentUserId //CustomerId from STRIPE PAYMENT GATEWAY
            : String? = null
    var locations: List<Location>? = null
    var healthAccounts: List<Long>? = null
    var departmentId: Long = 0
    var organizationId: Long = 0
    var organizationName: String? = null
    var departmentName: String? = null
    var officeLocationId: Long = 0
    var type //Self pay, Corporate, Partner Referral [applies to only member]
            : String? = null
    var source //Email, Website, Phone, Fax, Social Media, Other
            : String? = null
    var externalMemberId: String? = null
    var partnerVendorId: Long? = null
    var contractSigned = false
    var contractSignedDate: String? = null
    var deviceType: String? = null
    var deviceId: String? = null
    var SSN: String? = null
    var hobbies: String? = null
    var hireDate: String? = null
    var terminationDate: String? = null
    var employmentType: String? = null
    var compensationType: String? = null
    var compensation: String? = null
    var payFrequency: String? = null
    var employmentStatus: String? = null
    var safeHarborStatus = "Yellow" //Red/Green/Yellow
    var safeHarborTier: Int? = null
    var tobaccoUser: Boolean? = null
    var disabled: Boolean? = null
    var specialistLocations: List<Location>? = null
    var descriptions: List<VendorDescription>? = null


    var planSubscriptions:List<Subscription>?=ArrayList()


    //Added temporary
    var availableSlots: ArrayList<TimePeriodConverter>? = null
    var serviceId: Long? = null
}


class Specialist {
    var id: Long? = null
    var name: String? = null
    var nickname: String? = null
    var email: String? = null
    var isCorporateAdmin = false
    var isVendorAdmin = false
    var isVendor = false
    var mobilePhone: String? = null
    var textPhone: String? = null
    var landlinePhone: String? = null
    var imageIds: Set<Long>? = null
    var timeZone: String? = null

    //Redundant due to lastModifiedDate
    var lastAccessedDate: String? = null
    var authType = "NATIVE"

    /*TODO: need spelling correction */
    var prefferedLanguage: String? = null
    var status: String? = null

    var accessLevel: String? = null
    var specialistLocationIds: Set<Long>? = null
    var licenseToPracticeStates: Set<String>? = null
    var consultingFee: Float? = null
    var memberFee: Float? = null
    var serviceFee: Float? = null
    var avgHCCRiskScore: Float? = null
    var vendorId: Long = 0
    var tin: String? = null
    var npi: String? = null
    var vendorNPI: String? = null
    var videoBioId: Long? = null
    var specialization: List<Int>? = null
    var availableServiceDefinitionIds: List<Long>? = null
    var vendorName: String? = null
    var acceptingNewPatients: Boolean? = null

    // Not persistent fields: For IMPORT/EXPORT Purposes --Starts here
    var specializationNames: String? = null
    var affiliations: List<String>? = null
    var tier: Int? = null
    var tierScore: Double? = null
    var gender: String? = null
    var maritalStatus: String? = null
    var dateOfBirth: String? = null
    var healthAccounts: List<Long>? = null
    var deviceType: String? = null
    var deviceId: String? = null

    var specialistLocations: List<Location>? = null
    var descriptions: List<VendorDescription>? = null

    //Added temporary
    var availableSlots: ArrayList<TimePeriodConverter>? = null
    var serviceId: Long? = null
}


class Location{
    var streetAddress1: String? = null

    var streetAddress2: String? = null

    var city: String? = null

    var stateOrProvince: String? = null

    var zipCode: String? = null

    var country: String? = null

    var tag: String? = null
    var email: String? = null
    var phone: String? = null

    var locality: List<String>? = null

    var setting: String? = null

    var localities: HashMap<String, List<String>>? = null

    //var geoPt: GeoPt? = null

    var distance: Double? = null

    var geoZip: String? = null

    var geoZipDescription: String? = null

    var parentEntityName: String? = null

    var parentEntityId: Long? = null
}


class VendorDescription{
    var description: String? = null
    var vendorDescriptionType: VendorDescriptionTypeEnum? = null
}


class TimePeriodConverter{
    var startTime: String? = null
    var endTime: String? = null
}

class Speciality{
    var name:String?=null
    var value:Int?=null
}


class Reason(var id: Long, var name: String) {
    var synonyms: List<String>? = null
    var description: String? = null
    var imageIds: Set<Long>? = null
    var serviceDefinitionIds: Set<Long>? = null
    var diagnosisCodes: Set<String>? = null
    var serviceSettings: Set<String>? = null
    var episodes: Set<String>? = null
    var episodeCodes: Set<String>? = null
    var concernCategories: Set<String>? = null
    //var specialities: List<ReasonSpecialityMap>? = null
    var severity: String? = null
    var displayToMembers: Boolean? = null
    var demography: String? = null
}

class Service {
    var id: Long = 0

    var createdDate: String? = null

    var metaDataInstance: String? = null

    var htmlTemplate: String? = null

    // common attributes
    var wpServiceDefinitionId: Long = 0
    var wpVendorId: Long = 0
    var wpVendorUserId: Long = 0
    var vendorImageIds: Set<Long>? = null
    var vendorName: String? = null
    var wpName: String? = null
    var wpProjectName: String? = null
    var wpStatus: String? = null
    var wpCostPerHour:Double = 0.0
    var wpFixedCost:Double = 0.0
    var wpOpenToQuote:Boolean = false
    var wpOpenToOrder:Boolean = false
    var wpOpenToAppointment:Boolean = false
    var wpServicePaymentType: String? = null

    //public long wpLocationId;//deprecated in favor of wpLocationIds
    var wpLocationIds: Set<Long>? = null
    var wpDuration:Int = 0
    var wpImageIds: ArrayList<Long>? = null
    var wpIsDefault:Boolean = false
    var wpNumberOfAppointments :Int= 0
    var wpMRP: Double? = null
    var wpLeadTime: Double? = null
    var wpOverview: String? = null
    var wpSubType: List<String>? = null
    var wpIsOmniPresent:Boolean = false
    var serviceDefinitionName: String? = null

    var vendorLocations: List<Location>? = null
    //var columns: List<ColumnConverter>? = null
    var distance: Double? = null

    var vendorUserId: String? = null

    var wpCode: String? = null
    var wpUnits: Int? = null
    var wpSetting: String? = null
}

class Subscription {
    var coverageId: Long?=null
    var coverageLevel: String?=null
    var coveredMemberIds:List<Long>?=ArrayList()
    var directNetworkIds: List<Long>?=ArrayList()
    var endDate: String?=null
    var enrollmentDate: String?=null
    var episode: Long?=null
    var id:Long?=null
    var networkIds: List<Long>?=ArrayList()
    var organizationId: Long?=null
    var planCategoryId: Long?=null
    var planId: Long?=null
    var planName: String?=null
    var startDate: String?=null
    var  userId: Long?=null
}

class Appointment(id: Long = 0) {
    var isVendor = false
    var vendorId: Long = 0
    var vendorName: String? = null
    var userEmail: String? = null
    var serviceId: Long = 0
    var serviceName: String? = null
    var startTime: String? = null
    var endTime: String? = null
    var customerName: String? = null
    var customerEmail: String? = null
    var customerPhone: String? = null
    var id: Long = id
    var userId: String? = null
    var vendorUserId: String? = null
    var patientId: Long = 0
    var specialistId: Long = 0
    var specialistName: String? = null
    var status: String? = null
    var caseId: Long = 0
    var orderId: Long = 0
    var appointmentTrigger: String? = null
    var specialistGoogleUserId: String? = null
    var specialistImageId: Set<Long>? = null
    var specialistMobilePhone: String? = null
    var bookedAmount: Double? = null
    var reasonId: Long? = null
    var reasonName: String? = null
    var serviceSetting: String? = null
    var where: String? = null
    var issueDescription: String? = null
    var contactPreference: String? = null
    var issueImageIds: Set<Long>? = null

}

class NetworkCoverage  {
    var id: Long? = null
    var name: String? = null
    var networkType: String? = null
    var serviceCode: String? = null
    var serviceId: Long = 0
    var providerId: Long = 0
    var memberId: Long = 0
    var organizationId: Long = 0
    var totalProviderAmount = 0.0
    var patientPlanId: Long = 0
    var units = 0.0
    var groupedTotalProviderAmount: Map<String, Double> = HashMap()
    var totalPatientAmount = 0.0
    var groupedTotalPatientAmount: Map<String, Double> = HashMap()
    var providerAmountBreakup: Map<String, AmountDetail> = HashMap<String, AmountDetail>()
    var patientAmountBreakup: Map<String, AmountDetail> = HashMap<String, AmountDetail>()
    var patientAmountBreakupStructure: PriceBreakUp? = null
    var errorCode: String? = null
    var errorDescription: String? = null
    var service: Service? = null

}

class PriceBreakUp {
    var Insured: Double? = null
    var Sponsor: Double? = null
}

class AmountDetail  {
    var amount = 0.0
    var glName: String? = null
    var glType: String? = null
    var chargeCategory: String? = null
    var chargeType: String? = null
    var termType: String? = null
    var walletAccountType: String? = null
    var resource: String? = null
}


class AvailabilityGroupedByDate {
    var availableDate: String? = null
    var slots: List<TimePeriodConverter> = ArrayList()
}

class CostBreakUp {
    var negotiated: Double? = null
    var insured: Double? = null
    var sponsor: Double? = null
    var coverageId : Long? = null
}

class CalendarEvent {
    private val id: String? = null
    private val vendorUserId: Long = 0
    private val serviceId: Long = 0
    var eventSummary: String? = null
    var timezone: String? = null
    var appointment: Appointment? = null
    var vendorEventId: String? = null
    var errors: List<WPError>? = ArrayList()

}

class WPError {
    var errorCode = 0
    var fieldName: String? = null
    var message: String? = null
    var description: String? = null
    var errorType: String? = null
}

internal class Availability {
    var availableDate: String? = null
    var slots: List<TimePeriodConverter> = ArrayList()
}

class SpecialistAvailability{
    var eventSummary: String? = null
    var timezone: String? = null
    var duration: Int? = null
    var leadTime: Int? = null
    var numberOfAppointments: Int? = null
    var numberOfAvailbilities: Int? = null
    var busyTimePeriods: List<TimePeriodConverter> = ArrayList()
    var freeTimePeriods: List<TimePeriodConverter> = ArrayList()
    /*
        "busyTimePeriods": [],
        "freeTimePeriods": [{
        "startTime": "09-14-2017 11:30:00",
        "endTime": "09-14-2017 11:45:00"
        }, {
        "startTime": "09-14-2017 11:45:00",
        "endTime": "09-14-2017 12:00:00"
        }]
   */
}

class DeveloperAccount{
    var accountId: String? = null

    var name: String? = null

    var address: String? = null

    var appIds: List<Long>? = null

    var email: String? = null
    var imageId: Long? = null

    var phone: String? = null

    var website: String? = null

    var status: String? = null

    var privacyPolicyLink: String? = null

    var termsOfUseLink: String? = null

    var consentToCareLink: String? = null
}



class BrandTheme{
    var target : String? = null

    var brandLogoIds : Set<Long>? = null

    var fontFileIds: Set<Long>? = null

    var fontFileUrls: List<String>? = null

    //var fontFiles: List<FileInfoConverter>? = null

    // Body styles

    // Body styles
    var bodyFontFamily: String? = null

    var bodyFontSize // with unit as px, pt,em,rem etc.
            : String? = null

    var bodyFontColor: String? = null

    // General

    // General
    var primaryColor // button background, link color, etc.
            : String? = null

    var borderColor: String? = null

    var backgroundColor: String? = null

    // Reusable global UI Components

    // Buttons

    // Reusable global UI Components
    // Buttons
    var lightButtonBackgroundColor: String? = null

    var lightButtonTextColor: String? = null

    var darkButtonBackgroundColor: String? = null

    var darkButtonTextColor: String? = null

    // Hyperlinks

    // Hyperlinks
    var hyperlinkDefaultColor: String? = null

    var hyperlinkHoverColor: String? = null

    var hyperlinkActiveColor: String? = null

    // Badges/labels

    // Badges/labels
    var badgeTextColor: String? = null

    var badgeBackgroundColor: String? = null

    // Tabs

    // Tabs
    var tabBackgroundColor: String? = null

    var tabActiveBackgroundColor: String? = null

    var tabHoverBackgroundColor: String? = null

    var tabTextColor: String? = null

    var tabActiveTextColor: String? = null

    var tabHoverTextColor: String? = null

    // Text selection

    // Text selection
    var textSelectionColor: String? = null

    var textSelectionBackgroundColor: String? = null

    var description: String? = null

    var tenantId // organizationId/vendorId
            : Long? = null
}



enum class VendorDescriptionTypeEnum(val value: String) {
    BASIC("Basic"), PASSION("Passion"), DIFFERENTIATOR("Differentiator"), TRAINING("Training"), SHORT_DESCRIPTION(
        "ShortDescription"
    ),
    SPECIALIZATION(
        "Specialization"
    ),
    EDUCATION("Education"), PROFESSIONAL_EXPERIENCE("ProfessionalExperience"), LANGUAGES("Languages"), REGISTRATION(
        "Registration"
    ),
    AWARDS_AND_RECOGNITIONS("AwardsAndRecognitions"), MEMBERSHIPS("Memberships"), QUALIFICATION("Qualification"), BUSINESS_DESCRIPTION(
        "BusinessDescription"
    ),
    DENTAL_EXAMINATION("DentalExmination"), REMARKS("Remarks"), FOOD_ALLERGIES("FoodAllergies"), DRUG_ALLERGIES(
        "DrugAllergies"
    ),
    AIRBORNE_ALLERGIES("AirborneAllergies"), HISTORICAL_MEDICATION_DATA("HistoricalMedicationData"), HISTORICAL_HOSPITALIZATION_DATA(
        "HistoricalHospitalizationData"
    );

}