package com.spritehealth.sdk.model

import java.util.*


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
    var vendorDescriptionType: String? = null
}


class TimePeriodConverter{
    var startTime: String? = null
    var endTime: String? = null
}

class Speciality{
    var name:String?=null
    var value:Int?=null
}