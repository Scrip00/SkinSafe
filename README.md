# SkinSafe
SkinSafe is a project that allows you to monitor your skin health and not stress about any manifestations on the skin. A built-in neural network will detect one of the 6 most frequent illnesses and give you statistics. You may also track them using a convenient tracking system. Link to ML model: https://colab.research.google.com/drive/1VKr5pJo-kZvsCNNsg-VLdZ_A3OcuppbR?usp=sharing

Description:
SkinSafe is a project that allows you to monitor your skin health and not stress about any manifestations on the skin. A built-in neural network will detect one of the 6 most frequent illnesses and give you statistics. You may also track them using a convenient tracking system.

Inspiration:
One of the team members had a big brown spot on his back all his life. Every time he went to the doctors, they pointed to him and advised him to check for skin cancer. Ironically, when we trained the neural network and wrote the application, he tested it on himself and it turned out that it was just a harmless speck. Moreover, the description of the disease that the neural network gave out is very similar to the given case.

What it does:
With this application, you can quickly, and most importantly, accurately, find out the type of your formation, but just take a photo of it or upload it from your gallery! You will see all the possible options for the most popular skin diseases and their descriptions, as well as how they fit your situation. Thanks to the convenient search history, you can always revise the analysis results. And thanks to the new skin disease tracking system, you can always keep an eye on even the most suspicious lesions on your skin. To do that, you can add a new formation or edit an existing one and see how it changed over time! The places on your body where you have them are displayed on a home screen.

How we built it:
First, we wrote and trained a neural network using Python and Jupyter Notebook. In order to train it, we had to write many methods for data augmentation using Java, since there were clearly not enough photographs of some diseases. Next came the stage of developing an application for Android. First of all, we were able to import the neural network using Tensorflow Lite. And then there was a gradual development of the rest of the application, that is, the ability to take or import photos, save data using internal storage, show history, and, most importantly, the ability to track neoplasms.

Technologies we used:
Java
Python
AI/Machine Learning

Challenges we ran into:
Since this was the first experience of creating a complex application, our team faced the problem of lack of specific knowledge, which was quickly resolved thanks to Google.

Accomplishments we're proud of:
Importing a neural network into a mobile application is not an easy task. Moreover, the neural network had convolutional layers, which made life even more difficult for us. We are also proud that the accuracy of our model when recognizing one of the 6 most popular skin diseases is about 75%, as well as 90% when recognizing melanoma among moles. We are also proud of our tracking system, as we spent sleepless hours and missed meals on it. Moreover, it came out very useful and user-friendly.

What we've learned:
Development and debugging of applications for android, writing convolutional neural networks and preparing a dataset for them.

What's next:
We plan to finalize our project, improve some functions, slightly change the design and possibly release it on the play market.

Built with:
Android Studio, Jupyter Notebook, NetBeans, PyCharm
