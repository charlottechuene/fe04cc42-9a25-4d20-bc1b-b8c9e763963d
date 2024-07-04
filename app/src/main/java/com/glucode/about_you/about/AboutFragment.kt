package com.glucode.about_you.about

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.content.Intent
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.glucode.about_you.about.views.ProfileCardView
import com.glucode.about_you.about.views.QuestionCardView
import com.glucode.about_you.databinding.FragmentAboutBinding
import com.glucode.about_you.mockdata.MockData

class AboutFragment: Fragment() {
    private lateinit var binding: FragmentAboutBinding
    private lateinit var profileCardView: ProfileCardView
    private val REQUEST_IMAGE_PICK = 1
    private val REQUEST_PERMISSION_READ_EXTERNAL_STORAGE = 2


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAboutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setProfileCard()
        setUpQuestions()
    }

    private fun setProfileCard() {
        val name = arguments?.getString("name")
        val techRole = arguments?.getString("role")
        val engineer = MockData.engineers.firstOrNull { it.name == name }

        engineer?.let {
            profileCardView = ProfileCardView(requireContext())
            profileCardView.engineerName = name
            profileCardView.engineerRole = techRole
            profileCardView.year = it.quickStats.years.toString()
            profileCardView.coffee = it.quickStats.coffees.toString()
            profileCardView.bug = it.quickStats.bugs.toString()


            val defaultImageUri = it.defaultImageName
            if (defaultImageUri != null) {
                profileCardView.setImage(defaultImageUri)
            }

            binding.container.addView(profileCardView)

            profileCardView.imageView.setOnClickListener {
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, REQUEST_IMAGE_PICK)
            }
        }
    }


    private fun setUpQuestions() {
        val engineerName = arguments?.getString("name")
        val engineer = MockData.engineers.first { it.name == engineerName }

        engineer.questions.forEach { question ->
            val questionView = QuestionCardView(requireContext())
            questionView.title = question.questionText
            questionView.answers = question.answerOptions
            questionView.selection = question.answer.index

            binding.container.addView(questionView)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            val selectedImage: Uri? = data?.data
            selectedImage?.let {
                Log.d("AboutFragment", "Selected Image URI: $it")
                if (::profileCardView.isInitialized) {
                    profileCardView.setImage(it)
                    MockData.engineers.find { it.name == arguments?.getString("name") }?.defaultImageName = it
                    setProfileCard()
                }
            }
        }
    }
}



