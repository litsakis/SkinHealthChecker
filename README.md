# SkinHealthChecker
 SkinHealthChecker App detects possible melanoma skin cancer using OpenCV and Android camera.

SkinHealthChecker is an application that detect - identify skin diseases. 
Specifically the identification of skin moles and their
categorization into healthy and possibly problematic. For this purpose, an android application
was created using the OpenCv library.
In particular, the library of android camera api was used to optimize the immortalization of
the skin. Then, with the help of the OpenCv library, the skin image is processed appropriately
to extract the localized objects. After that with the correct categorization with different
criteria, one of the objects is selected and this object is the skin mole.
With the help of OpenCv and its’ functions again the characteristics of the mole (size, shape,
color variability, symmetry) are extracted.
By importing information from the user about the specific mole, as well as by proceeding to
the exported characteristics, the mole is diagnosed.
The application enables the storage of characteristics - preserving history of the mole in a
database for future viewing as well as controlling its evolution. Finally, there is the ability to
manage profiles so that it can be used by many users on the same device, with each profile
having its own independent data! The user of each profile can send the application's data
directly to a dermatologist who can contact the application user with an email or phone.
