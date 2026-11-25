
package com.faculdade.sistemachamados.controller;

import com.faculdade.sistemachamados.model.Chamado;
import com.faculdade.sistemachamados.model.Usuario;
import com.faculdade.sistemachamados.model.Tramitacao;
import com.faculdade.sistemachamados.repository.ChamadoRepository;
import com.faculdade.sistemachamados.repository.UsuarioRepository;
import com.faculdade.sistemachamados.repository.TramitacaoRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class PainelTecnicoController {

    private final ChamadoRepository chamadoRepository;
    private final UsuarioRepository usuarioRepository;
    private final TramitacaoRepository tramitacaoRepository;

    public PainelTecnicoController(
            ChamadoRepository chamadoRepository,
            UsuarioRepository usuarioRepository,
            TramitacaoRepository tramitacaoRepository) {

        this.chamadoRepository = chamadoRepository;
        this.usuarioRepository = usuarioRepository;
        this.tramitacaoRepository = tramitacaoRepository;
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
        List<Chamado> filaAtendimento = chamados.stream()
                .filter(c -> "ABERTO".equals(c.getStatus())
                        || "EM_ANDAMENTO".equals(c.getStatus()))
                .toList();

        List<Chamado> historicoFinalizados = chamados.stream()
                .filter(c -> "FINALIZADO".equals(c.getStatus()))
                .toList();

        model.addAttribute("filaAtendimento", filaAtendimento);
        model.addAttribute("historicoFinalizados", historicoFinalizados);
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

        List<Tramitacao> tramitacoes =
                tramitacaoRepository
                        .findByChamadoIdOrderByDataTramitacaoAsc(id);

        model.addAttribute("nomeTecnico", usuarioNome);
        model.addAttribute("chamado", chamado);
        model.addAttribute("tramitacoes", tramitacoes);

        return "detalhes-chamado";
    }

    @PostMapping("/tecnico/chamados/{id}/assumir")
    public String assumirChamado(
            @PathVariable("id") Long id,
            HttpSession session) {

        Long usuarioId = (Long) session.getAttribute("usuarioId");
        String usuarioPerfil = (String) session.getAttribute("usuarioPerfil");

        if (usuarioId == null || !"TECNICO".equals(usuarioPerfil)) {
            return "redirect:/";
        }

        Usuario tecnico = usuarioRepository.findById(usuarioId)
                .orElse(null);

        if (tecnico == null || !"TECNICO".equals(tecnico.getPerfil())) {
            session.invalidate();
            return "redirect:/";
        }

        Chamado chamado = chamadoRepository.findById(id)
                .orElse(null);

        if (chamado == null) {
            return "redirect:/tecnico";
        }

        if ("ABERTO".equals(chamado.getStatus())
                && chamado.getTecnico() == null) {

            chamado.setTecnico(tecnico);
            chamado.setStatus("EM_ANDAMENTO");

            chamadoRepository.save(chamado);
        }

        return "redirect:/tecnico/chamados/" + id;
    }

    @PostMapping("/tecnico/chamados/{id}/tramitar")
    public String tramitarChamado(
            @PathVariable("id") Long id,
            @RequestParam("descricao") String descricao,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Long usuarioId = (Long) session.getAttribute("usuarioId");
        String usuarioPerfil = (String) session.getAttribute("usuarioPerfil");

        if (usuarioId == null || !"TECNICO".equals(usuarioPerfil)) {
            return "redirect:/";
        }

        Usuario tecnico = usuarioRepository.findById(usuarioId)
                .orElse(null);

        if (tecnico == null || !"TECNICO".equals(tecnico.getPerfil())) {
            session.invalidate();
            return "redirect:/";
        }

        Chamado chamado = chamadoRepository.findById(id)
                .orElse(null);

        if (chamado == null) {
            return "redirect:/tecnico";
        }

        if (!"EM_ANDAMENTO".equals(chamado.getStatus())
                || chamado.getTecnico() == null
                || !usuarioId.equals(chamado.getTecnico().getId())) {

            redirectAttributes.addFlashAttribute(
                    "erro", "Você não pode tramitar este chamado."
            );

            return "redirect:/tecnico/chamados/" + id;
        }

        if (descricao.isBlank() || descricao.length() > 2000) {

            redirectAttributes.addFlashAttribute(
                    "erro", "Informe uma descrição de até 2000 caracteres."
            );

            return "redirect:/tecnico/chamados/" + id;
        }

        Tramitacao tramitacao = new Tramitacao();

        tramitacao.setChamado(chamado);
        tramitacao.setTecnico(tecnico);
        tramitacao.setDescricao(descricao.trim());
        tramitacao.setDataTramitacao(LocalDateTime.now());

        tramitacaoRepository.save(tramitacao);

        redirectAttributes.addFlashAttribute(
                "sucesso", "Tramitação registrada com sucesso."
        );

        return "redirect:/tecnico/chamados/" + id;
    }


    @PostMapping("/tecnico/chamados/{id}/concluir")
    public String concluirChamado(
            @PathVariable("id") Long id,
            @RequestParam("solucao") String solucao,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Long usuarioId = (Long) session.getAttribute("usuarioId");
        String usuarioPerfil = (String) session.getAttribute("usuarioPerfil");

        if (usuarioId == null || !"TECNICO".equals(usuarioPerfil)) {
            return "redirect:/";
        }

        Usuario tecnico = usuarioRepository.findById(usuarioId)
                .orElse(null);

        if (tecnico == null || !"TECNICO".equals(tecnico.getPerfil())) {
            session.invalidate();
            return "redirect:/";
        }

        Chamado chamado = chamadoRepository.findById(id)
                .orElse(null);

        if (chamado == null) {
            return "redirect:/tecnico";
        }

        if (!"EM_ANDAMENTO".equals(chamado.getStatus())
                || chamado.getTecnico() == null
                || !usuarioId.equals(chamado.getTecnico().getId())) {

            redirectAttributes.addFlashAttribute(
                    "erro", "Você não pode concluir este chamado."
            );

            return "redirect:/tecnico/chamados/" + id;
        }

        if (solucao.isBlank() || solucao.length() > 2000) {

            redirectAttributes.addFlashAttribute(
                    "erro", "Informe uma solução de até 2000 caracteres."
            );

            return "redirect:/tecnico/chamados/" + id;
        }

        chamado.setSolucao(solucao.trim());
        chamado.setStatus("FINALIZADO");
        chamado.setDataConclusao(LocalDateTime.now());

        chamadoRepository.save(chamado);

        redirectAttributes.addFlashAttribute(
                "sucesso", "Chamado finalizado com sucesso."
        );

        return "redirect:/tecnico/chamados/" + id;
    }

}
