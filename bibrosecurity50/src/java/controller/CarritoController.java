/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

/**
 *
 * @author dansan
 */
@ManagedBean(name = "carritoController")
@SessionScoped

public class CarritoController implements Serializable {

    @EJB
    private SaleOrderLineFacade saleOrderLineFacade;
    @EJB
    private SaleOrderFacade saleOrderFacade;
    private ProductController productController;
    //private UsuarioController usuarioController;
    private ArrayList<Product> carrito = null;
    private Product productoSelecionado;
    private int idProductoSeleccionado;
    private double totalCompra = 0.0;

    public CarritoController() {
        carrito = new ArrayList<Product>();
        productController = new ProductController();
    }

    //metodos
    public String agregarAlCarrito(int idProducto) {
        setIdProductoSeleccionado(idProducto);
        Product p = buscarProductoCarrito(idProductoSeleccionado);
        if (p != null && p.getQuantity()> 0) {
            int n = carrito.indexOf(p);
            carrito.get(n).setQuantity(carrito.get(n).getQuantity() + 1);
            totalCompra+=p.getPurchasePrice();
        } else {
                productoSelecionado = productController.doBuscarProductoParaElCarrito(idProducto);
                productoSelecionado.setQuantity(1);
                if(productoSelecionado.getQtyAvailable()>0){
                    carrito.add(productoSelecionado);
                    totalCompra+=productoSelecionado.getPurchasePrice();
                }        
        }        
        return "carritoactual";
    }

    private Product buscarProductoCarrito(int idProducto) {
        Product p = null;
        for (Product prod : carrito) {
            if (prod.getId() == idProducto) {
                p = prod;
                break;
            }
        }
        return p;
    }

    public String borrarDelCarrito(int idProducto) {
        boolean encontrado = false;
        int i = 0;
        while (!encontrado) {
            if (carrito.get(i).getId() == idProducto) {
                totalCompra-=carrito.get(i).getPurchasePrice();
                carrito.remove(i);                
                encontrado = true;
            }
            i++;
        }
        return "carritoactual";
    }

    public String confirmarPedido() {
        Date date = new Date();
        ArrayList<SaleOrderLine> pedidos = new ArrayList<SaleOrderLine>();
//        if (usuarioController.getIdcliente() > 0) {
//            Pedido pedido = new Pedido(date, usuarioController.getIdcliente(), 'a');
            SaleOrder pedido = new SaleOrder(date, "a");
            for (Product p : carrito) {
                Number n = p.getQuantity();
                BigDecimal cant = new BigDecimal(n.toString());
                BigDecimal purchase_price = new BigDecimal(String.valueOf(p.getPurchasePrice()));
                BigDecimal monto = purchase_price.multiply(cant);
                BigDecimal descuento = new BigDecimal(0.0);
                if (p.getQuantity() >= 20) {
                    descuento = monto.multiply(new BigDecimal(0.1));
                }
                SaleOrderLine linea = 
                        new SaleOrderLine(
                                pedido.getId(),
                                p.getId(),
                                p.getQuantity(),
                                p.getPurchasePrice(),
                                monto.doubleValue());
                saleOrderLineFacade.create(linea);
            }
            //pedido.setPedidos(pedidos);
            saleOrderFacade.create(pedido);            
            carrito.clear();
            totalCompra = 0.0;
            return "confirmacion";
//        } else {
//            FacesContext faces = FacesContext.getCurrentInstance();
//            faces.addMessage("mensajeError", new FacesMessage("Para poder comprar debes registrate o ingresar al sistema"));
//            return "carrito";
//        }
    }

    public String doIrCarrito() {
        productController.doListarTodosProductos();
        return "List";
    }

    public String doIrCarritoActual() {
        return "carritoactual";
    }

    public ArrayList<Product> getCarrito() {
        return carrito;
    }

    public void setCarrito(ArrayList<Product> carrito) {
        this.carrito = carrito;
    }

    public Product getProductoSelecionado() {
        return productoSelecionado;
    }

    public void setProductoSelecionado(Product productoSelecionado) {
        this.productoSelecionado = productoSelecionado;
    }

    public int getIdProductoSeleccionado() {
        return idProductoSeleccionado;
    }

    public void setIdProductoSeleccionado(int idProductoSeleccionado) {
        this.idProductoSeleccionado = idProductoSeleccionado;
    }

    public SaleOrderLineFacade getLineapedidoFacade() {
        return saleOrderLineFacade;
    }

    public void setLineapedidoFacade(SaleOrderLineFacade saleOrderLineFacade) {
        this.saleOrderLineFacade = saleOrderLineFacade;
    }

    public SaleOrderFacade getPedidoFacade() {
        return saleOrderFacade;
    }

    public void setPedidoFacade(SaleOrderFacade pedidoFacade) {
        this.saleOrderFacade = saleOrderFacade;
    }

    public ProductController getProductoController() {
        return productController;
    }

    public void setProductoController(ProductController productController) {
        this.productController = productController;
    }

//    public UsuarioController getUsuarioController() {
//        return usuarioController;
//    }
//
//    public void setUsuarioController(UsuarioController usuarioController) {
//        this.usuarioController = usuarioController;
//    }

    public double getTotalCompra() {
        return totalCompra;
    }

    public void setTotalCompra(double totalCompra) {
        this.totalCompra = totalCompra;
    }
  
}