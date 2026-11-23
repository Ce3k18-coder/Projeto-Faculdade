
package com.faculdade.sistemachamados.controller;

import com.faculdade.sistemachamados.model.Chamado;
import com.faculdade.sistemachamados.repository.ChamadoRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class PainelAlunoController {

    private final ChamadoRepository chamadoRepository;

    public PainelAlunoController(ChamadoRepository chamadoRepository) {
        this.chamadoRepository = chamadoRepository;
    }

    @GetMapping("/aluno")
    public String painelAluno(HttpSession session, Model model) {

        Long usuarioId = (Long) session.getAttribute("usuarioId");
        String usuarioNome = (String) session.getAttribute("usuarioNome");
        String usuarioPerfil = (String) session.getAttribute("usuarioPerfil");

        if (usuarioId == null || !"ALUNO".equals(usuarioPerfil)) {
            return "redirect:/";
        }

        List<Chamado> chamados =
                chamadoRepository.findByAlunoId(usuarioId);

        long abertos = chamados.stream()
                .filter(c -> "ABERTO".equals(c.getStatus()))
                .count();

        long andamento = chamados.stream()
                .filter(c -> "EM_ANDAMENTO".equals(c.getStatus()))
                .count();

        long finalizados = chamados.stream()
                .filter(c -> "FINALIZADO".equals(c.getStatus()))
                .count();

        model.addAttribute("nomeAluno", usuarioNome);
        model.addAttribute("chamados", chamados);
        model.addAttribute("totalChamados", chamados.size());
        model.addAttribute("chamadosAbertos", abertos);
        model.addAttribute("chamadosAndamento", andamento);
        model.addAttribute("chamadosFinalizados", finalizados);

        return "painel-aluno";
    }

    @GetMapping("/aluno/novo-chamado")
    public String novoChamado(HttpSession session) {

        Long usuarioId = (Long) session.getAttribute("usuarioId");
        String usuarioPerfil = (String) session.getAttribute("usuarioPerfil");

        if (usuarioId == null || !"ALUNO".equals(usuarioPerfil)) {
            return "redirect:/";
        }

        return "novo-chamado";
    }

}
