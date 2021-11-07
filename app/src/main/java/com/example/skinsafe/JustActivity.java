package com.example.skinsafe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;

public class JustActivity extends AppCompatActivity {
    TextView textName, textDescription;
    ImageView imageView1, imageView2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_just);
        textName = findViewById(R.id.textName);
        textDescription = findViewById(R.id.textDescription);
        imageView1 = findViewById(R.id.imageView1);
        imageView2 = findViewById(R.id.imageView2);

        Intent intent = this.getIntent();
        String name = intent.getStringExtra("type");
        switch (name){
            case "Actinic Keratosis":
                textName.setText("Actinic Keratosis, may be malignant");
                textName.setTextColor(Color.YELLOW);
                textDescription.setText("An actinic keratosis (ak-TIN-ik ker-uh-TOE-sis) is a rough, scaly patch on the skin that develops from years of sun exposure. It's often found on the face, lips, ears, forearms, scalp, neck or back of the hands.\n" +
                        "\n" +
                        "Also known as a solar keratosis, an actinic keratosis grows slowly and usually first appears in people over 40. You can reduce your risk of this skin condition by minimizing your sun exposure and protecting your skin from ultraviolet (UV) rays.\n" +
                        "\n" +
                        "Left untreated, the risk of actinic keratoses turning into a type of skin cancer called squamous cell carcinoma is about 5% to 10%.");
                try {
                    imageView1.setImageBitmap(BitmapFactory.decodeStream(getAssets().open("actinic keratosis1.jpg")));
                    imageView2.setImageBitmap(BitmapFactory.decodeStream(getAssets().open("actinic keratosis2.jpg")));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "Basal Cell Carcinoma":
                textName.setText("Basal Cell Carcinoma, malignant");
                textName.setTextColor(Color.RED);
                textDescription.setText("Basal cell carcinoma is a type of skin cancer. Basal cell carcinoma begins in the basal cells — a type of cell within the skin that produces new skin cells as old ones die off.\n" +
                        "\n" +
                        "Basal cell carcinoma often appears as a slightly transparent bump on the skin, though it can take other forms. Basal cell carcinoma occurs most often on areas of the skin that are exposed to the sun, such as your head and neck.\n" +
                        "\n" +
                        "Most basal cell carcinomas are thought to be caused by long-term exposure to ultraviolet (UV) radiation from sunlight. Avoiding the sun and using sunscreen may help protect against basal cell carcinoma.");
                try {
                    imageView1.setImageBitmap(BitmapFactory.decodeStream(getAssets().open("basal cell carcinoma1.jpg")));
                    imageView2.setImageBitmap(BitmapFactory.decodeStream(getAssets().open("basal cell carcinoma2.jpg")));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "Melanoma":
                textName.setText("Melanoma, malignant");
                textName.setTextColor(Color.RED);
                textDescription.setText("Melanoma, the most serious type of skin cancer, develops in the cells (melanocytes) that produce melanin — the pigment that gives your skin its color. Melanoma can also form in your eyes and, rarely, inside your body, such as in your nose or throat.\n" +
                        "\n" +
                        "The exact cause of all melanomas isn't clear, but exposure to ultraviolet (UV) radiation from sunlight or tanning lamps and beds increases your risk of developing melanoma. Limiting your exposure to UV radiation can help reduce your risk of melanoma.\n" +
                        "\n" +
                        "The risk of melanoma seems to be increasing in people under 40, especially women. Knowing the warning signs of skin cancer can help ensure that cancerous changes are detected and treated before the cancer has spread. Melanoma can be treated successfully if it is detected early.");
                try {
                    imageView1.setImageBitmap(BitmapFactory.decodeStream(getAssets().open("melanoma1.jpg")));
                    imageView2.setImageBitmap(BitmapFactory.decodeStream(getAssets().open("melanoma2.jpg")));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "Nevus":
                textName.setText("Nevus, benign");
                textDescription.setText("Nevus (plural: nevi) is the medical term for a mole. Nevi are very common. Most peopleTrusted Source have between 10 and 40. Common nevi are harmless collections of colored cells. They typically appear as small brown, tan, or pink spots.\n" +
                        "\n" +
                        "You can be born with moles or develop them later. Moles that you’re born with are known as congenital moles. However, most moles develop during childhood and adolescence. This is known as an acquired nevus. Moles can also develop later in life as a result of sun exposure.");
                try {
                    imageView1.setImageBitmap(BitmapFactory.decodeStream(getAssets().open("nevus1.jpg")));
                    imageView2.setImageBitmap(BitmapFactory.decodeStream(getAssets().open("nevus2.jpg")));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "Seborrheic Keratosis":
                textName.setText("Seborrheic Keratosis, benign");
                textDescription.setText("A seborrheic keratosis (seb-o-REE-ik ker-uh-TOE-sis) is a common noncancerous skin growth. People tend to get more of them as they get older.\n" +
                        "\n" +
                        "Seborrheic keratoses are usually brown, black or light tan. The growths look waxy, scaly and slightly raised. They usually appear on the head, neck, chest or back.\n" +
                        "\n" +
                        "Seborrheic keratoses are harmless and not contagious. They don't need treatment, but you may decide to have them removed if they become irritated by clothing or you don't like how they look.");
                try {
                    imageView1.setImageBitmap(BitmapFactory.decodeStream(getAssets().open("seborrheic keratosis1.jpg")));
                    imageView2.setImageBitmap(BitmapFactory.decodeStream(getAssets().open("seborrheic keratosis2.jpg")));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "Squamous Cell Carcinoma":
                textName.setText("Squamous Cell Carcinoma, malignant");
                textName.setTextColor(Color.RED);
                textDescription.setText("Squamous cell carcinoma of the skin is a common form of skin cancer that develops in the squamous cells that make up the middle and outer layers of the skin.\n" +
                        "\n" +
                        "Squamous cell carcinoma of the skin is usually not life-threatening, though it can be aggressive. Untreated, squamous cell carcinoma of the skin can grow large or spread to other parts of your body, causing serious complications.\n" +
                        "\n" +
                        "Most squamous cell carcinomas of the skin result from prolonged exposure to ultraviolet (UV) radiation, either from sunlight or from tanning beds or lamps. Avoiding UV light helps reduce your risk of squamous cell carcinoma of the skin and other forms of skin cancer.\n" +
                        "\n" +
                        "Squamous cells are found in many places in your body, and squamous cell carcinoma can occur anywhere squamous cells are found. Squamous cell carcinoma of the skin refers to cancer that forms in the squamous cells found in the skin.");
                try {
                    imageView1.setImageBitmap(BitmapFactory.decodeStream(getAssets().open("squamous cell carcinoma1.jpg")));
                    imageView2.setImageBitmap(BitmapFactory.decodeStream(getAssets().open("squamous cell carcinoma2.jpg")));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }

    }
}