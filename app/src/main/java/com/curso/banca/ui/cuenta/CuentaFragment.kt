package com.curso.banca.ui.cuenta

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.curso.banca.data.repository.AuthRepository
import com.curso.banca.databinding.FragmentCuentaBinding
import com.curso.banca.ui.auth.LoginActivity
import com.curso.banca.util.toLongDate
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class CuentaFragment : Fragment() {
    private var _binding: FragmentCuentaBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CuentaViewModel by viewModels()
    private val pickImage = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
        uri?.let {
            viewModel.updatePhoto(it.toString())
            Snackbar.make(binding.root, "Foto guardada en el perfil.", Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCuentaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnEditPhoto.setOnClickListener {
            pickImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        binding.btnConfiguracion.setOnClickListener {
            Snackbar.make(binding.root, "Configuración fuera de alcance del proyecto.", Snackbar.LENGTH_SHORT).show()
        }
        binding.btnCerrarSesion.setOnClickListener { confirmLogout() }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                val profile = state.profile
                val name = profile?.nombreCompleto?.takeIf { it.isNotBlank() } ?: "Usuario"
                binding.tvUserName.text = name
                binding.tvUserEmail.text = profile?.email ?: AuthRepository.currentUser()?.email.orEmpty()
                binding.tvNombreCompleto.text = name
                binding.tvCelular.text = profile?.celular.orEmpty()
                binding.tvFechaNacimiento.text = profile?.fechaNacimiento?.takeIf { it > 0 }?.toLongDate().orEmpty()
                binding.imgAvatar.text = name.firstOrNull()?.uppercaseChar()?.toString() ?: "M"
            }
        }
        viewModel.observe()
    }

    private fun confirmLogout() {
        AlertDialog.Builder(requireContext())
            .setTitle("Cerrar sesión")
            .setMessage("¿Quieres salir de MiBanco?")
            .setPositiveButton("Salir") { _, _ ->
                AuthRepository.logout()
                val intent = Intent(requireContext(), LoginActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
