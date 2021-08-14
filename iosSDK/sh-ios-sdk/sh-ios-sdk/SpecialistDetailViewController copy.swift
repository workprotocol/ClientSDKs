//
//  SpecialistDetailViewController.swift
//  sdk
//
//  Created by Riya Balhara on 29/06/21.
//

import UIKit

struct detailViewModel {
    let primaryText : String
    let secondaryText: String
    let numberOfLines: Int
   
}

 class detailLabel: UIButton {
        private let primaryLabel: UILabel = {
            let label = UILabel()
            label.numberOfLines=1
            label.textAlignment = .left;//.center
            label.textColor = .lightGray
            label.font = .systemFont(ofSize: 20, weight: .semibold)
            return label
        }()
    private let secondaryLabel: UITextView = {
        let label = UITextView()
        
        //label.textAlignment = .left;//.center
        label.textColor = .black
        label.font = .systemFont(ofSize: 20, weight: .regular)
        //label.numberOfLines = 0
        label.sizeToFit()
        
        return label
    }()
    
    
   
    

    override init(frame: CGRect) {
        super.init(frame: frame)
        addSubview(primaryLabel)
        addSubview(secondaryLabel)
        
        clipsToBounds = true
        //layer.cornerRadius = 8
        //layer.borderWidth = 2
        //layer.borderColor = UIColor.systemGray5.cgColor
        //backgroundColor = .systemBackground
        backgroundColor = .white
        //backgroundColor = UIColor.init(red: 100, green: 200, blue: 240, alpha: 1)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    func configure(with viewModel:detailViewModel)
    {
        primaryLabel.text = viewModel.primaryText
        secondaryLabel.text = viewModel.secondaryText
        
       /* if( viewModel.numberOfLines == 0){
            secondaryLabel.numberOfLines = 0
            secondaryLabel.sizeToFit()
        }
        else {
            secondaryLabel.numberOfLines = viewModel.numberOfLines
        }
 */
        
    }
    
 

    override func layoutSubviews() {
        super.layoutSubviews()
       
        primaryLabel.frame = CGRect(x: 0, y: 0, width: frame.size.width, height: 30)
        secondaryLabel.frame = CGRect(x: 0, y: 30, width: frame.size.width, height: frame.size.height-30    )
      
    }
}


class SpecialistDetailViewController: UIViewController {
    
    @IBOutlet weak var detailContentView: UIView!
    @IBOutlet weak var detailsStackView: UIStackView!
    @IBOutlet weak var genderLabel: UILabel!
    @IBOutlet weak var qualificationLabel: UILabel!
    @IBOutlet weak var navBar: UINavigationBar!
    @IBOutlet weak var educationLabel: UILabel!
    @IBOutlet weak var briefSummaryLabel: UILabel!
    var specialistId:String?
    var specialistName:String?
    var specialistImageUrl:String?
    var specialistArrIncoming: [String :String]?
    var serviceId: String?
    
    @IBOutlet weak var viewInsideScrollView: UIView!
    @IBOutlet weak var detailScrollView: UIScrollView!
    @IBOutlet weak var specialistImage: UIImageView!
    @IBOutlet weak var specialistLabel: UILabel!
    public init(specialistArr : [String : String]) {
        specialistArrIncoming = specialistArr
        specialistId = specialistArr["id"]
        specialistName = specialistArr["name"]
        specialistImageUrl = specialistArr["imageUrl"]
        serviceId = specialistArr["serviceId"]
        super.init(nibName: "SpecialistDetailViewController", bundle: Bundle(for: SpecialistDetailViewController.self))
    }
    func convertToDictionary(text: String) -> Any? {

     if let data = text.data(using: .utf8) {
         do {
            // return try JSONSerialization.jsonObject(with: data, options: []) as? Any
         
            let json = try? JSONSerialization.jsonObject(with: data as Data, options: []) as? Any
            return json
         } catch {
             print(error.localizedDescription)
         }
     }

     return nil
    }
    
    
    @IBOutlet weak var navigationLabel: UILabel!
    @IBOutlet weak var callParentController: UIButton!
    override func viewDidLoad() {
        super.viewDidLoad()
        
        //detailScrollView.contentSize = CGSize(width: Int(self.view.frame.size.width), height: 500)
       // detailContentView.superview?.bringSubviewToFront(detailContentView)
        //detailsStackView.superview?.bringSubviewToFront(detailsStackView)
        let myActivityIndicator = UIActivityIndicatorView(style: UIActivityIndicatorView.Style.medium)
                       
                       // Position Activity Indicator in the center of the main view
                       myActivityIndicator.center = view.center
               
                       // If needed, you can prevent Acivity Indicator from hiding when stopAnimating() is called
                       myActivityIndicator.hidesWhenStopped = true
                       
                       // Start Activity Indicator
                       myActivityIndicator.startAnimating()
               view.addSubview(myActivityIndicator)
        
        specialistLabel.text = specialistName
        let cmObj = SpriteHealthClient()
        cmObj.specialistDetail(specialistId: String(specialistId!)){ (error, result) in
            if let error = error {
                print(error)
            } else {
            // do stuff with the result
            let resultConverted = result.replacingOccurrences(of: "\\", with: "")
            if let json = self.convertToDictionary(text: resultConverted) as? AnyObject {

               print(json);
                print(type(of: json))
                DispatchQueue.main.async {
                self.displayDetails(json: json)
                    myActivityIndicator.stopAnimating()
                }
            }
            }
        }
        
        var urlString = specialistImageUrl!
        specialistImage.downloaded(from: urlString)
        // Do any additional setup after loading the view.
    }
    @IBAction func navigationBack(_ sender: Any) {
        self.presentingViewController?.dismiss(animated: true, completion: nil)
    }
    
    
    func displayDetails(json: AnyObject) {
        detailScrollView.isScrollEnabled = true
        detailScrollView.isUserInteractionEnabled = false
        let screenSize: CGRect = detailScrollView.bounds//UIScreen.main.bounds
        let xTopLeft = 0
        
        let topmargin = 0
        var count = 0;
        var briefSummary = ""
        var genderTxt = "";
        var educationTxt = ""
        var qualificationTxt = ""
        if let gender = json["gender"] as? String {
            //genderLabel.text = gender
            //AddLebelAndDescInScrollView(labelTxt:"GENDER", desc:gender)
            genderTxt = gender;
            }
        if let descriptions = json["descriptions"] as? [NSDictionary] {
           
            
            for description in descriptions
            {
                if (description["vendorDescriptionType"] as! String == "EDUCATION"){
                    let education =  description["description"] as! String
                    
                    
                    let educationArr: [String] = education.components(separatedBy: "$$")
                    print(type(of: educationArr))
                    if(educationArr.count > 0)
                    {
                        var educationLabeltxt = "";
                        if(educationArr.count == 3)
                        {
                            educationLabeltxt = educationArr[0] + " from " + educationArr[1] + " in " + educationArr[2]
                            
                        }
                        
                        else if(educationArr.count == 2)
                        {
                            educationLabeltxt = educationArr[0] + " from " + educationArr[1]
                        }
                        educationTxt = educationLabeltxt
                        
                        
                        //qualificationLabel.text = educationArr[0]
                        qualificationTxt = educationArr[0]
                       
 
                    }
                }
                if (description["vendorDescriptionType"] as! String == "SHORT_DESCRIPTION"){
                   briefSummary = description["description"] as! String
                    /*  briefSummaryLabel.sizeToFit()
                    briefSummaryLabel.text = briefSummary
                    briefSummaryLabel.textAlignment = .justified
                    */
                   

                }
            }
         
          
        }
        
        if(genderTxt != "")
        {
            let yTopLeft = topmargin + count*70
            let button = detailLabel(frame: CGRect(x: xTopLeft, y: yTopLeft, width: Int(screenSize.width-40), height: 60))
            button.configure(with: detailViewModel(primaryText: "GENDER", secondaryText: genderTxt, numberOfLines:1))
            viewInsideScrollView.addSubview(button)
            count += 1
        }
        if(qualificationTxt != ""){
            let yTopLeft = topmargin + count*70
            let qualLabel = detailLabel(frame: CGRect(x: xTopLeft, y: yTopLeft, width: Int(screenSize.width-40), height: 60))
            qualLabel.configure(with: detailViewModel(primaryText: "QUALIFICATION", secondaryText: qualificationTxt,numberOfLines:1))
            viewInsideScrollView.addSubview(qualLabel)
            count += 1
        }
        var offset = 0;
        if(educationTxt != "") {
            var yTopLeft = topmargin + count*70
            let eduLabel = detailLabel(frame: CGRect(x: xTopLeft, y: yTopLeft, width: Int(screenSize.width-40), height: 90))
            eduLabel.configure(with: detailViewModel(primaryText: "EDUCATION", secondaryText: educationTxt,numberOfLines : 2))
            viewInsideScrollView.addSubview(eduLabel)
            count += 1
            offset = 30
        }
        
        if(briefSummary != "") {
         
            let yTopLeft = topmargin + count*70 + offset
             let briefSummaryLabel = detailLabel(frame: CGRect(x: xTopLeft, y: yTopLeft, width: Int(screenSize.width-40), height: 300))
            briefSummaryLabel.configure(with: detailViewModel(primaryText: "BRIEF SUMMARY", secondaryText: briefSummary,numberOfLines:0))
            viewInsideScrollView.addSubview(briefSummaryLabel)
             
        }
        let height = topmargin + count*70 + 30 + 300
        viewInsideScrollView.frame = CGRect(x: 0,y: 0, width: Int(detailScrollView.bounds.size.width), height: 2000)
        var bound1 = self.detailScrollView.contentSize.height
        //self.viewInsideScrollView.frame.size.height = CGFloat(height+1000)
        var bound2 = self.detailScrollView.contentSize.height
        var bound3 = self.detailScrollView.contentSize.height
        
    }
    @IBAction func ParentAppCalled(_ sender: Any) {
        
      /*  print("parent app callback")
      SpriteHealthClient.gobackToParent();
    
        self.presentingViewController?.presentingViewController?.dismiss(animated: true, completion: nil)
        */
        let newViewController = AppointmentBookingViewController(specialistArr: specialistArrIncoming!)
        newViewController.modalPresentationStyle = .fullScreen
        self.present(newViewController, animated:true,completion:nil)
         
    }
    
    
    public init() {
        super.init(nibName: "SpecialistDetailViewController", bundle: Bundle(for: SpecialistDetailViewController.self))
    }
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destination.
        // Pass the selected object to the new view controller.
    }
    */
   
}



   
