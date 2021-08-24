//
//  DataModel.swift
//  sh-ios-sdk
//
//  Created by Ashish Balhara on 20/08/21.
//

import Foundation

public struct AccessTokenResponse: Codable{

    var token_type: String?

    var access_token: String?

    var refresh_token: String?

    var expires_in:  Int?

    var scope: String?

    //Custom attributes

    var isSSO: Bool?

    //Service Mesh purpose attributes
    var isValid: Bool?

    var userIdentity: String?
    init(dictionary: [String: Any]) throws {
       
        self = try JSONDecoder().decode(AccessTokenResponse.self, from: JSONSerialization.data(withJSONObject: dictionary))
        
       
    }
}



public enum IntegrationMode   {
    case LIVE
    case TEST
}

public struct APIDomains {
    static var LIVEurl = "api.spritehealth.com"
    static var TESTurl = "api-qa.spritehealth.com"
}

public struct WebClientDomains {
    static var LIVEurl = "app.spritehealth.com"
    static var TESTurl = "berger.spritehealth.com"
}
public class InitOption {
    var clientId:String?
    var userIdentity:String?
    var integrationMode:IntegrationMode?
    public init(clientId: String, userIdentity: String, integrationMode: IntegrationMode) {
        self.clientId=clientId
        self.userIdentity=userIdentity
        self.integrationMode=integrationMode
    }
    
}


public struct Location : Codable{
    var streetAddress1: String?

    var streetAddress2: String?

    var city: String?

    var stateOrProvince: String?

    var zipCode: String?

    var country: String?

    var tag: String?
    var email: String?
    var phone: String?

    var locality: [String]?

    var setting: String?

   // var localities:[ [String,String]]?

    //var geoPt: GeoPt?

    var distance: Double?

    var geoZip: String?

    var geoZipDescription: String?

    var parentEntityName: String?

    var parentEntityId:  Int?
}


public struct VendorDescription: Codable{
    var description: String?
    var vendorDescriptionType: String?
}


public struct TimePeriodConverter: Codable{
    var startTime: String?
    var endTime: String?
}
public struct Subscription: Codable {
    var coverageId: Int?
    var coverageLevel: String?
    var coveredMemberIds:[Int]?
    var directNetworkIds: [Int]?
    var endDate: String?
    var enrollmentDate: String?
    var episode: Int?
    var id:Int?
    var networkIds: [Int]?
    var organizationId: Int?
    var planCategoryId: Int?
    var planId: Int?
    var planName: String?
    var startDate: String?
    var  userId: Int?
}


public struct User: Codable {
    var id:  Int
    var name: String?
    var nickname: String?
    var email: String
    var isCorporateAdmin : Bool? = false
    var isVendorAdmin: Bool? = false
    var isVendor = false
    var assignmentStatus: String?
    var mobilePhone: String?
    var textPhone: String?
    var landlinePhone: String?
    var imageIds: [Int]?
    var timeZone: String?

    //Redundant due to lastModifiedDate
    var lastAccessedDate: String?
    //var authType = "NATIVE"

    /*TODO: need spelling correction */
    var prefferedLanguage: String?
    var status: String?

    /*===========
    =============Specialist Role fields
    ============================*/
    var accessLevel: String?
    var specialistLocationIds: [Int]?
    var licenseToPracticeStates: [String]?
    var consultingFee: Float?
    var memberFee: Float?
    var serviceFee: Float?
    var avgHCCRiskScore: Float?
    var vendorId: Int? = 0
    var tin: String?
    var npi: String?
    var vendorNPI: String?
    var videoBioId:  Int?
    var specialization: [Int]?
    var availableServiceDefinitionIds: [Int]?
    var vendorName: String?
    var acceptingNewPatients: Bool?

    // Not persistent fields: For IMPORT/EXPORT Purposes --Starts here
    var specializationNames: String?
    var affiliations: [String]?
    var tier: Int?
    var tierScore: Double?
    var accountableProviderId:  Int?
    var gender: String?
    var maritalStatus: String?
    var dateOfBirth: String?
    var walletId: Int? = 0
    var familyId: String?

    var employeeId: String?
    var designation: String?
    var insuranceAccount: String?
    var insurance: String?
    var groupNumber: String?
    var membershipId: String?
    var familyRelation: String?
    var referralSource: String?
    var contactPreference: String?
    var recommendedPlanId: Int? = 0
    var recommendedPlanName: String?
    var healthAccountAccess : Bool? = true //default true
    var lastCreditAmount :Double? = 0.0
    var paymentUserId : String? //CustomerId from STRIPE PAYMENT GATEWAY
            
    var locations: [Location]?
    var healthAccounts: [Int]?
    var departmentId: Int? = 0
    var organizationId: Int? = 0
    var organizationName: String?
    var departmentName: String?
    var officeLocationId: Int? = 0
    var officeLocations:  [Location]?
    var type: String? //Self pay, Corporate, Partner Referral [applies to only member]
            
    var source: String? //Email, Website, Phone, Fax, Social Media, Other
            
    var externalMemberId: String?
    var partnerVendorId:  Int?
    var contractSigned :Bool? = false
    var contractSignedDate: String?
    var deviceType: String?
    var deviceId: String?
    var SSN: String?
    var hobbies: String?
    var hireDate: String?
    var terminationDate: String?
    var employmentType: String?
    var compensationType: String?
    var compensation: String?
    var payFrequency: String?
    var employmentStatus: String?
    var safeHarborStatus : String? = "Yellow" //Red/Green/Yellow
    var safeHarborTier: Int?
    var tobaccoUser: Bool?
    var disabled: Bool?
    var specialistLocations: [Location]?
    var descriptions: [VendorDescription]?


    var planSubscriptions:[Subscription]? = []


    //Added temporary
    var availableSlots: [[String:String]]?
    var serviceId:  Int?
    
    init(dictionary: [String: Any]) throws {
       
        self = try JSONDecoder().decode(User.self, from: JSONSerialization.data(withJSONObject: dictionary))
        
       
    }
   
}


public struct  Specialist: Codable {
    var id: Int
    var name: String?
    var nickname: String?
    var email: String?
    var isCorporateAdmin: Bool?  = false
    var isVendorAdmin: Bool?  = false
    var isVendor: Bool?  = false
    var mobilePhone: String?
    var textPhone: String?
    var landlinePhone: String?
    var imageIds: [Int]?
    var timeZone: String?

    //Redundant due to lastModifiedDate
    var lastAccessedDate: String?
    var authType: String? = "NATIVE"

    /*TODO: need spelling correction */
    var prefferedLanguage: String?
    var status: String?

    var accessLevel: String?
    var specialistLocationIds: [Int]?
    var licenseToPracticeStates: [String]?
    var consultingFee: Float?
    var memberFee: Float?
    var serviceFee: Float?
    var avgHCCRiskScore: Float?
    var vendorId: Int? = 0
    var tin: String?
    var npi: String?
    var vendorNPI: String?
    var videoBioId: Int?
    var specialization: [Int]?
    var availableServiceDefinitionIds: [Int]?
    var vendorName: String?
    var acceptingNewPatients: Bool?

    // Not persistent fields: For IMPORT/EXPORT Purposes --Starts here
    var specializationNames: String?
    var affiliations: [String]?
    var tier: Int?
    var tierScore: Double?
    var gender: String?
    var maritalStatus: String?
    var dateOfBirth: String?
    var healthAccounts: [Int]?
    var deviceType: String?
    var deviceId: String?

    var specialistLocations: [Location]?
    var descriptions: [VendorDescription]?

    //Added temporary
    var availableSlots: [TimePeriodConverter]?
    var serviceId: Int?
    
    init(dictionary: [String: Any]) throws {
       
        self = try JSONDecoder().decode(Specialist.self, from: JSONSerialization.data(withJSONObject: dictionary))
        
       
    }
}





public struct Speciality: Codable{
    var name:String?
    var value:Int?
    init(dictionary: [String: Any]) throws {
        self = try JSONDecoder().decode(Speciality.self, from: JSONSerialization.data(withJSONObject: dictionary))
    }
}


public struct Reason: Codable {
    var id: Int
    var name: String
    var synonyms: [String]?
    var description: String?
    var imageIds: [Int]?
    var serviceDefinitionIds: [Int]?
    var diagnosisCodes: [String]?
    var serviceSettings: [String]?
    var episodes: [String]?
    var episodeCodes: [String]?
    var concernCategories: [String]?
    //var specialities: List<ReasonSpecialityMap>?
    var severity: String?
    var displayToMembers: Bool?
    var demography: String?
}

public struct Service: Codable {
    var id: Int = 0

    var createdDate: String?

    var metaDataInstance: String?

    var htmlTemplate: String?

    // common attributes
    var wpServiceDefinitionId: Int = 0
    var wpVendorId: Int = 0
    var wpVendorUserId: Int = 0
    var vendorImageIds: [Int]?
    var vendorName: String?
    var wpName: String?
    var wpProjectName: String?
    var wpStatus: String?
    var wpCostPerHour:Double = 0.0
    var wpFixedCost:Double = 0.0
    var wpOpenToQuote:Bool = false
    var wpOpenToOrder:Bool = false
    var wpOpenToAppointment:Bool = false
    var wpServicePaymentType: String?

    //public Int wpLocationId;//deprecated in favor of wpLocationIds
    var wpLocationIds: [Int]?
    var wpDuration:Int = 0
    var wpImageIds: [Int]?
    var wpIsDefault:Bool = false
    var wpNumberOfAppointments :Int = 0
    var wpMRP: Double?
    var wpLeadTime: Double?
    var wpOverview: String?
    var wpSubType: [String]?
    var wpIsOmniPresent:Bool = false
    var serviceDefinitionName: String?

    var vendorLocations: [Location]?
    //var columns: List<ColumnConverter>?
    var distance: Double?

    var vendorUserId: String?

    var wpCode: String?
    var wpUnits: Int?
    var wpSetting: String?
}



public struct Appointment: Codable {
    var id: Int
    var isVendor: Bool? = false
    var vendorId: Int? = 0
    var vendorName: String?
    var userEmail: String?
    var serviceId: Int? = 0
    var serviceName: String?
    var startTime: String?
    var endTime: String?
    var customerName: String?
    var customerEmail: String?
    var customerPhone: String?
    var userId: String?
    var vendorUserId: String?
    var patientId: Int? = 0
    var specialistId: Int? = 0
    var specialistName: String?
    var status: String?
    var caseId: Int? = 0
    var orderId: Int? = 0
    var appointmentTrigger: String?
    var specialistGoogleUserId: String?
    var specialistImageId: [Int]?
    var specialistMobilePhone: String?
    var bookedAmount: Double?
    var reasonId:  Int?
    var reasonName: String?
    var serviceSetting: String?
    //var where: String?
    var issueDescription: String?
    var contactPreference: String?
    var issueImageIds: [Int]?

}
public struct PriceBreakUp: Codable {
    var Insured: Double?
    var Sponsor: Double?
}
public struct  AmountDetail : Codable  {
    var amount: Float? = 0.0
    var glName: String?
    var glType: String?
    var chargeCategory: String?
    var chargeType: String?
    var termType: String?
    var walletAccountType: String?
    var resource: String?
}
public struct NetworkCoverage: Codable  {
    var id:  Int?
    var name: String?
    var networkType: String?
    var serviceCode: String?
    var serviceId: Int? = 0
    var providerId: Int? = 0
    var memberId: Int? = 0
    var organizationId: Int? = 0
    var totalProviderAmount: Float?
    var patientPlanId: Int? = 0
    var units : Float?
    //var groupedTotalProviderAmount: [[String,Double]]?
    var totalPatientAmount: Float?
    var groupedTotalPatientAmount : [String : Double]?
    //var providerAmountBreakup: [String, AmountDetail]?
    //var patientAmountBreakup: [String, Double]?
    var patientAmountBreakupStructure: PriceBreakUp?
    var errorCode: String?
    var errorDescription: String?
    var service: Service?

}
 




public struct  AvailabilityGroupedByDate: Codable {
    var availableDate: String?
    //var slots: List<TimePeriodConverter> = ArrayList()
}

public struct CostBreakUp : Codable {
    var negotiated: Double?
    var insured: Double?
    var sponsor: Double?
    var coverageId :  Int?
}

public struct  CalendarEvent: Codable {
    var id: String?
    var vendorUserId: Int? = 0
    var serviceId: Int? = 0
    var eventSummary: String?
    var timezone: String?
    var appointment: Appointment?
    var vendorEventId: String?
    var errors: [WPError]?

}

public struct   WPError: Codable {
    var errorCode: Int? = 0
    var fieldName: String?
    var message: String?
    var description: String?
    var errorType: String?
}

public struct  Availability: Codable  {
    var availableDate: String?
    var slots: [TimePeriodConverter]
    
}

public struct  SpecialistAvailability: Codable {
    var eventSummary: String?
    var timezone: String?
    var duration: Int?
    var leadTime: Int?
    var numberOfAppointments: Int?
    var numberOfAvailbilities: Int?
    var busyTimePeriods: [TimePeriodConverter]?
    var freeTimePeriods: [TimePeriodConverter]?
  
}

public struct   DeveloperAccount: Codable {
    var accountId: String?

    var name: String?

    var address: String?

    var appIds: [Int]?

    var email: String?
    var imageId:  Int?

    var phone: String?

    var website: String?

    var status: String?

    var privacyPolicyLink: String?

    var termsOfUseLink: String?

    var consentToCareLink: String?
}



public struct  BrandTheme: Codable{
    var target : String?

    var brandLogoIds : [Int]?

    var fontFileIds: [Int]?

    var fontFileUrls: [String]?

    var bodyFontFamily: String?

    var bodyFontSize // with unit as px, pt,em,rem etc.
            : String?

    var bodyFontColor: String?

    var primaryColor  : String?

    var borderColor: String?

    var backgroundColor: String?

   
    var lightButtonBackgroundColor: String?

    var lightButtonTextColor: String?

    var darkButtonBackgroundColor: String?

    var darkButtonTextColor: String?

    var hyperlinkDefaultColor: String?

    var hyperlinkHoverColor: String?

    var hyperlinkActiveColor: String?

    var badgeTextColor: String?

    var badgeBackgroundColor: String?

    var tabBackgroundColor: String?

    var tabActiveBackgroundColor: String?

    var tabHoverBackgroundColor: String?

    var tabTextColor: String?

    var tabActiveTextColor: String?

    var tabHoverTextColor: String?

    var textSelectionColor: String?

    var textSelectionBackgroundColor: String?

    var description: String?

    var tenantId   :  Int?
}


/*
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
 */
