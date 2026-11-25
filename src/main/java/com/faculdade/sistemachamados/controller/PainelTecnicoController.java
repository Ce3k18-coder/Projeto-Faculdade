
package com.faculdade.sistemachamados.controller;

import com.faculdade.sistemachamados.model.Chamado;
import com.faculdade.sistemachamados.repository.ChamadoRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class PainelTecnicoController {

    private final ChamadoRepository chamadoRepository;

    public PainelTecnicoController(ChamadoRepository chamadoRepository) {
        this.chamadoRepository = chamadoRepository;
    }

    @GetMapping("/tecnico")
    public String painelTecnico(HttpSession session, Model model) {

        Long usuarioId = (Long) session.getAttribute("usuarioId");
        String usuarioNome = (String) session.getAttribute("usuarioNome");
        String usuarioPerfil = (String) session.getAttribute("usuarioPerfil");

        if (usuarioId == null || !"TECNICO".equals(usuarioPerfil)) {
            return "redirect:/";
        }

        List<Chamado> chamados = chamadoRepository.findAll();

        long abertos = chamados.stream()
                .filter(c -> "ABERTO".equals(c.getStatus()))
                .count();

        long andamento = chamados.stream()
                .filter(c -> "EM_ANDAMENTO".equals(c.getStatus()))
                .count();

        long finalizados = chamados.stream()
                .filter(c -> "FINALIZADO".equals(c.getStatus()))
                .count();

        model.addAttribute("nomeTecnico", usuarioNome);
        model.addAttribute("chamados", chamados);
        model.addAttribute("totalChamados", chamados.size());
        model.addAttribute("chamadosAbertos", abertos);
        model.addAttribute("chamadosAndamento", andamento);
        model.addAttribute("chamadosFinalizados", finalizados);

        return "painel-tecnico";
    }

    @GetMapping("/tecnico/chamados/{id}")
    public String visualizarChamado(
            @PathVariable("id") Long id,
            HttpSession session,
            Model model) {

        Long usuarioId = (Long) session.getAttribute("usuarioId");
        String usuarioNome = (String) session.getAttribute("usuarioNome");
        String usuarioPerfil = (String) session.getAttribute("usuarioPerfil");

        if (usuarioId == null || !"TECNICO".equals(usuarioPerfil)) {
            return "redirect:/";
        }

        Chamado chamado = chamadoRepository.findById(id)
                .orElse(null);

        if (chamado == null) {
            return "redirect:/tecnico";
        }

        model.addAttribute("nomeTecnico", usuarioNome);
        model.addAttribute("chamado", chamado);

        return "detalhes-chamado";
    }
}
